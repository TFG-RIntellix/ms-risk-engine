package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.services.RiskMetricsCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies.ModelEndpointResolver;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies.ScoringModelExecutionStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDraft;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.InvalidFormChangesException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.input.SimulationDraftPortService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.FetchScoringPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskIndicatorCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskMetricsCalculationContext;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.FinancialMetricsCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.MapUtilities;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationFieldNames;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case that orchestrates stateless simulation draft calculations.
 *
 * Implements the SimulationDraftPortService (domain input port) to properly
 * expose
 * the use case functionality through the hexagonal architecture boundary.
 *
 * Follows the same pattern as ScoringProcessingService:
 * - Fetches base scoring data via output ports
 * - Merges form changes with base features
 * - Invokes model prediction asynchronously (via ModelPredictionPort)
 * - Returns simulation draft with metrics and deltas
 *
 * @author Lucía Fernández Mancebo
 */
@Service
@Slf4j
public class CalculateSimulationDraftUseCase implements SimulationDraftPortService {

    private final FetchScoringPort fetchScoringPort;
    private final List<ScoringModelExecutionStrategy> scoringModelExecutionStrategies;
    private final RiskMetricsCalculationService metricsCalculationService;
    private final RiskIndicatorCalculationService riskIndicatorCalculationService;

    /**
     * Constructor of the CalculateSimulationDraftUseCase class.
     *
     * @param fetchScoringPort                output port to fetch scoring data.
     * @param scoringModelExecutionStrategies the available model execution
     *                                        strategies for determining
     *                                        model endpoints based on request type.
     * @param metricsCalculationService       service to calculate risk metrics
     *                                        (orchestrates model invocation and
     *                                        calculation).
     * @param riskIndicatorCalculationService domain service for calculating risk
     *                                        indicators
     *                                        (DTI and LTV).
     */
    public CalculateSimulationDraftUseCase(
            final FetchScoringPort fetchScoringPort,
            final List<ScoringModelExecutionStrategy> scoringModelExecutionStrategies,
            final RiskMetricsCalculationService metricsCalculationService,
            final RiskIndicatorCalculationService riskIndicatorCalculationService) {
        this.fetchScoringPort = Objects.requireNonNull(fetchScoringPort);
        this.scoringModelExecutionStrategies = Objects.requireNonNull(scoringModelExecutionStrategies);
        this.metricsCalculationService = Objects.requireNonNull(metricsCalculationService);
        this.riskIndicatorCalculationService = Objects.requireNonNull(riskIndicatorCalculationService);
    }

    /**
     * Calculates a simulation draft without persistence.
     *
     * Process flow (following hexagonal architecture):
     * 1. Fetch base scoring data for the request
     * 2. Merge base features with form changes to create simulation input
     * 3. Fire async model prediction call to obtain new PD
     * 4. Calculate deltas by comparing simulated metrics with base metrics
     *
     * @param requestId   the request identifier.
     * @param formChanges the user-specified modified values.
     * @return the simulation draft with simulated metrics and deltas.
     * @throws ScoringNotFoundException    if scoring data cannot be retrieved
     * @throws InvalidFormChangesException if form changes are invalid
     */
    @Override
    public SimulationDraft calculateDraft(final String requestId, final String requestType,
            final FormChanges formChanges) {

        if (formChanges == null || formChanges.getValues() == null || formChanges.getValues().isEmpty()) {
            throw new InvalidFormChangesException("Any formChanges are required");
        }

        // Retrieve base scoring - contains input snapshot and base metrics
        final Scoring baseScoring = fetchScoringPort.fetchByRequestId(requestId);
        if (baseScoring == null || baseScoring.getResults() == null) {
            throw new ScoringNotFoundException(LogMessage.SCORING_RETRIEVING_MESSAGE_EXCEPTION + requestId);
        }

        // Extract base variables for delta calculation
        final Map<String, Object> baseVariables = extractBaseVariables(baseScoring);

        // Merge base input features with form changes to create simulation input
        final Map<String, Object> mergedVariables = mergeData(baseScoring, formChanges);

        // CRITICAL: Recalculate DTI and LTV BEFORE model invocation
        // These are critical input features that influence the model prediction
        riskIndicatorCalculationService.recalculateRiskIndicators(
                mergedVariables, requestType, baseScoring.getInputSnapshot());

        final RiskMetricsCalculationContext context = new RiskMetricsCalculationContext(
                mergedVariables,
                requestId,
                resolveModelEndpointPath(requestType),
                requestType);

        // Calculate simulated metrics using the reusable service
        // This orchestrates model invocation and all metric calculations in parallel
        final var result = metricsCalculationService.calculateRiskMetrics(context);
        final RiskMetrics simulatedMetrics = result.getRiskMetrics();

        // Calculate delta between base and simulated metrics
        final SimulationDelta delta = buildDelta(baseScoring.getResults(), simulatedMetrics, baseVariables,
                mergedVariables);

        return new SimulationDraft(simulatedMetrics, delta);
    }

    /**
     * Extracts base variables from the scoring input snapshot for delta
     * calculation.
     * 
     * @param baseScoring the base scoring containing input features.
     * @return a Map with base variable values.
     */
    private Map<String, Object> extractBaseVariables(final Scoring baseScoring) {
        final Map<String, Object> baseVariables = baseScoring.getInputSnapshot() != null
                ? new HashMap<>(baseScoring.getInputSnapshot())
                : new HashMap<>();
        return baseVariables;
    }

    /**
     * Resolves the model endpoint path based on request type.
     * 
     * Delegates to ModelEndpointResolver to determine which AI model
     * should be invoked (Loan, Mortgage, or Credit Card).
     * This ensures consistency between scoring and simulation workflows.
     *
     * @param requestType the request type (PRESTAMO, HIPOTECA, TARJETA_CREDITO)
     * @return the appropriate model endpoint path.
     * @throws IllegalArgumentException if request type is not supported.
     */
    private String resolveModelEndpointPath(final String requestType) {
        return ModelEndpointResolver.resolveEndpointPath(requestType, scoringModelExecutionStrategies);
    }

    /**
     * Merges the base scoring input features with the user-provided
     * form changes to create a comprehensive set of variables for PD prediction.
     *
     * @param baseScoring the base scoring data containing the original input
     *                    snapshot and metrics.
     * @param formChanges the user-specified modified values that should override
     *                    the base scoring features.
     * @return a Map containing the merged variables to be used for PD prediction
     *         and simulation calculations.
     */
    private Map<String, Object> mergeData(final Scoring baseScoring, final FormChanges formChanges) {
        final Map<String, Object> mergedData = new HashMap<>();

        final Map<String, Object> baseInputs = baseScoring.getInputSnapshot();
        if (baseInputs == null || baseInputs.isEmpty()) {
            throw new ScoringNotFoundException(LogMessage.SCORING_RETRIEVING_MESSAGE_EXCEPTION);
        }

        mergedData.putAll(baseInputs); // Start with base input features

        // Override with form changes (user modifications take precedence)
        mergedData.putAll(formChanges.getValues());

        return mergedData;
    }

    /**
     * Builds the simulation delta by comparing base metrics with simulated metrics.
     * Delta represents the change in key metrics between the base scenario and the
     * simulated scenario.
     * 
     * @param baseMetrics      the original risk metrics from base scoring.
     * @param simulatedMetrics the new metrics from simulation.
     * @param baseVariables    the original feature values.
     * @param mergedVariables  the merged feature values (includes form changes).
     * @return the SimulationDelta with all calculated changes.
     */
    private SimulationDelta buildDelta(
            final RiskMetrics baseMetrics,
            final RiskMetrics simulatedMetrics,
            final Map<String, Object> baseVariables,
            final Map<String, Object> mergedVariables) {

        final double basePrincipal = MapUtilities.getDouble(baseVariables,
                SimulationFieldNames.MODEL_FIELD_REQUESTED_AMOUNT, 0);
        final double baseAnnualRate = MapUtilities.getDouble(baseVariables,
                SimulationFieldNames.MODEL_FIELD_INTEREST_RATE, 0);
        final int baseTermMonths = (int) MapUtilities.getDouble(baseVariables,
                SimulationFieldNames.MODEL_FIELD_TERM_MONTHS,
                SimulationConstants.MIN_TERM_MONTHS);
        final double baseAnnualIncome = MapUtilities.getDouble(baseVariables,
                SimulationFieldNames.MODEL_FIELD_ANNUAL_INCOME, 0);

        // Calculate simulated financial metrics for comparison
        final double simPrincipal = MapUtilities.getDouble(mergedVariables,
                SimulationFieldNames.MODEL_FIELD_REQUESTED_AMOUNT,
                basePrincipal);
        final double simAnnualRate = MapUtilities.getDouble(mergedVariables,
                SimulationFieldNames.MODEL_FIELD_INTEREST_RATE,
                baseAnnualRate);
        final int simTermMonths = (int) MapUtilities.getDouble(mergedVariables,
                SimulationFieldNames.MODEL_FIELD_TERM_MONTHS,
                baseTermMonths);
        final double simAnnualIncome = MapUtilities.getDouble(mergedVariables,
                SimulationFieldNames.MODEL_FIELD_ANNUAL_INCOME,
                baseAnnualIncome);

        // Calculate base financial metrics for comparison
        final double baseMonthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(basePrincipal,
                baseAnnualRate, baseTermMonths);
        final double baseDti = FinancialMetricsCalculator.calculateDti(baseMonthlyPayment, baseAnnualIncome);

        // Calculate simulated financial metrics for comparison
        final double simMonthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(simPrincipal,
                simAnnualRate, simTermMonths);
        final double simDti = FinancialMetricsCalculator.calculateDti(simMonthlyPayment, simAnnualIncome);

        // Build delta
        final SimulationDelta delta = new SimulationDelta();
        delta.setPdChange(
                SimulationConstants.getSafe(simulatedMetrics.getProbabilityOfDefault())
                        - SimulationConstants.getSafe(baseMetrics.getProbabilityOfDefault()));
        delta.setEclChange(SimulationConstants.getSafe(simulatedMetrics.getExpectedCalculatedLoss())
                - SimulationConstants.getSafe(baseMetrics.getExpectedCalculatedLoss()));
        final String baseRiskGradeName = baseMetrics.getRiskLevel() != null ? baseMetrics.getRiskLevel() : "UNKNOWN";
        final String simRiskGradeName = simulatedMetrics.getRiskLevel() != null ? simulatedMetrics.getRiskLevel()
                : "UNKNOWN";
        delta.setRiskGradeChange(baseRiskGradeName + SimulationConstants.RISK_GRADE_ARROW + simRiskGradeName);
        delta.setMonthlyPaymentChange(simMonthlyPayment - baseMonthlyPayment);
        delta.setDtiChange(simDti - baseDti);

        return delta;
    }

}
