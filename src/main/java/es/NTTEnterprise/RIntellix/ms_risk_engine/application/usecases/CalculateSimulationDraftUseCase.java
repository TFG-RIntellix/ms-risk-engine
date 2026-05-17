package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.*;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.InvalidFormChangesException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.input.SimulationDraftPortService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.FetchScoringPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.ModelPredictionPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.FinancialMetricsCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case that orchestrates stateless simulation draft calculations.
 *
 * Implements the SimulationDraftPortService (domain input port) to properly expose
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

    private static final String REQUESTED_AMOUNT_KEY = "requestedAmount";
    private static final String INTEREST_RATE_KEY = "interestRate";
    private static final String TERM_MONTHS_KEY = "termMonths";
    private static final String ANNUAL_INCOME_KEY = "annualIncome";

    private final FetchScoringPort fetchScoringPort;
    private final ModelPredictionPort modelPredictionPort;

    /**
     * Constructor of the CalculateSimulationDraftUseCase class.
     *
     * @param fetchScoringPort               output port to fetch scoring data.
     * @param modelPredictionPort            output port to invoke model predictions.
     */
    public CalculateSimulationDraftUseCase(
            final FetchScoringPort fetchScoringPort,
            final ModelPredictionPort modelPredictionPort) {
        this.fetchScoringPort = Objects.requireNonNull(fetchScoringPort);
        this.modelPredictionPort = Objects.requireNonNull(modelPredictionPort);
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
     * @throws ScoringNotFoundException if scoring data cannot be retrieved
     * @throws InvalidFormChangesException if form changes are invalid
     */
    @Override
    public SimulationDraft calculateDraft(final String requestId, final FormChanges formChanges) {

        if (formChanges == null || formChanges.getValues() == null || formChanges.getValues().isEmpty()) {
            throw new InvalidFormChangesException("formChanges is required");
        }

        // Retrieve base scoring - contains input snapshot and base metrics
        final Scoring baseScoring = fetchScoringPort.fetchByRequestId(requestId);
        if (baseScoring == null || baseScoring.getResults() == null) {
            throw new ScoringNotFoundException(LogMessage.SCORING_RETRIEVING_MESSAGE_EXCEPTION + requestId);
        }

        // Extract base variables for delta calculation
        final Map<String, Object> baseVariables = extractBaseVariables(baseScoring);

        // Merge base input features with form changes to create simulation input payload
        final Map<String, Object> mergedVariables = mergeData(baseScoring, formChanges);

        // Invoke model asynchronously to obtain new PD prediction
        final CompletableFuture<ModelPredictionResult> predictionResultFuture = modelPredictionPort
                .predictAsync(
                        mergedVariables,
                        requestId,
                        resolveModelEndpointPath());

        // Calculate rest of the metrics in parallell like we do with the scoring.

        // Wait for model prediction result
        final ModelPredictionResult predictionResult = predictionResultFuture.join();

        // Build simulated metrics from base metrics and new PD prediction
        final SimulationMetrics simulatedMetrics = buildSimulatedMetrics(
                predictionResult.getProbabilityOfDefault(),
                baseScoring.getResults(),
                mergedVariables);

        // Calculate delta between base and simulated metrics
        final SimulationDelta delta = buildDelta(baseScoring.getResults(), simulatedMetrics, baseVariables);

        return new SimulationDraft(simulatedMetrics, delta);
    }

    /**
     * Extracts base variables from the scoring input snapshot for delta calculation.
     * 
     * @param baseScoring the base scoring containing input features.
     * @return a Map with base variable values.
     */
    private Map<String, Object> extractBaseVariables(final Scoring baseScoring) {
        final Map<String, Object> baseVariables = new HashMap<>();
        if (baseScoring.getInputSnapshot() != null && baseScoring.getInputSnapshot().getFeatures() != null) {
            baseVariables.putAll(baseScoring.getInputSnapshot().getFeatures());
        }
        return baseVariables;
    }

    /**
     * Resolves the model endpoint path.
     * For now, defaults to loan endpoint.
     *
     * @return the appropriate model endpoint path.
     */
    private String resolveModelEndpointPath() {
        return "/api/v1/risk/predict-loan"; // default
    }

    /**
     * Merges the base scoring input features with the user-provided
     * form changes to create a comprehensive set of variables for PD prediction.
     *
     * @param baseScoring the base scoring data containing the original input snapshot and metrics.
     * @param formChanges the user-specified modified values that should override the base scoring features.
     * @return a Map containing the merged variables to be used for PD prediction and simulation calculations.
     */
    private Map<String, Object> mergeData(final Scoring baseScoring, final FormChanges formChanges) {
        final Map<String, Object> mergedData = new HashMap<>();

        final ModelInputs baseInputs = baseScoring.getInputSnapshot();
        if (baseInputs == null || baseInputs.getFeatures() == null 
                || baseInputs.getFeatures().isEmpty()) {
            throw new ScoringNotFoundException(LogMessage.SCORING_RETRIEVING_MESSAGE_EXCEPTION);
        }

        // Start with base features
        mergedData.putAll(baseInputs.getFeatures());

        // Override with form changes (user modifications take precedence)
        mergedData.putAll(formChanges.getValues());

        return mergedData;
    }

    /**
     * Builds simulated metrics from the base metrics, new PD prediction, and merged variables.
     * Includes financial metrics like monthly payment, DTI, total payment, etc.
     *
     * @param newPd the new probability of default from the model.
     * @param baseMetrics the base risk metrics from the original scoring.
     * @param mergedVariables the merged variables containing loan parameters.
     * @return the SimulationMetrics with all calculated values.
     */
    private SimulationMetrics buildSimulatedMetrics(
            final Double newPd,
            final RiskMetrics baseMetrics,
            final Map<String, Object> mergedVariables) {

        final double principal = getDouble(mergedVariables, REQUESTED_AMOUNT_KEY, 0);
        final double annualRate = getDouble(mergedVariables, INTEREST_RATE_KEY, 0);
        final int termMonths = (int) getDouble(mergedVariables, TERM_MONTHS_KEY, SimulationConstants.MIN_TERM_MONTHS);
        final double annualIncome = getDouble(mergedVariables, ANNUAL_INCOME_KEY, 0);

        // Calculate financial metrics
        final double monthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(principal, annualRate, termMonths);
        final double dti = FinancialMetricsCalculator.calculateDti(monthlyPayment, annualIncome);
        final double totalPayment = FinancialMetricsCalculator.calculateTotalPayment(monthlyPayment, termMonths);
        final double totalInterest = FinancialMetricsCalculator.calculateTotalInterest(totalPayment, principal);
        final double disposableIncome = FinancialMetricsCalculator.calculateDisposableIncome(annualIncome, monthlyPayment);

        // Calculate risk metrics using new PD and base LGD/EAD
        final double pd = newPd == null ? baseMetrics.getProbabilityOfDefault() : newPd;
        final double lgd = baseMetrics.getLossGivenDefault() != null ? baseMetrics.getLossGivenDefault() : SimulationConstants.ZERO_VALUE;
        final double ead = principal > 0 ? principal : (baseMetrics.getExposureAtDefault() != null ? baseMetrics.getExposureAtDefault() : SimulationConstants.ZERO_VALUE);
        final double ecl = pd * lgd * ead;

        // Build simulation metrics
        final SimulationMetrics metrics = new SimulationMetrics();
        metrics.setPd(pd);
        metrics.setLgd(lgd);
        metrics.setEad(ead);
        metrics.setEcl(ecl);
        if (baseMetrics.getRiskLevel() != null) {
            metrics.setRiskGrade(baseMetrics.getRiskLevel());
        }
        metrics.setMonthlyPayment(monthlyPayment);
        metrics.setDti(dti);
        metrics.setTotalPayment(totalPayment);
        metrics.setTotalInterest(totalInterest);
        metrics.setDisposableIncome(disposableIncome);

        return metrics;
    }

    /**
     * Builds the simulation delta by comparing base metrics with simulated metrics.
     * Delta represents the change in key metrics between the base scenario and the simulated scenario.
     * 
     * @param baseMetrics the original risk metrics from base scoring.
     * @param simulatedMetrics the new metrics from simulation.
     * @param baseVariables the original feature values.
     * @return the SimulationDelta with all calculated changes.
     */
    private SimulationDelta buildDelta(
            final RiskMetrics baseMetrics,
            final SimulationMetrics simulatedMetrics,
            final Map<String, Object> baseVariables) {
        
        final double basePrincipal = getDouble(baseVariables, REQUESTED_AMOUNT_KEY, 0);
        final double baseAnnualRate = getDouble(baseVariables, INTEREST_RATE_KEY, 0);
        final int baseTermMonths = (int) getDouble(baseVariables, TERM_MONTHS_KEY, SimulationConstants.MIN_TERM_MONTHS);
        final double baseAnnualIncome = getDouble(baseVariables, ANNUAL_INCOME_KEY, 0);

        // Calculate base financial metrics for comparison
        final double baseMonthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(basePrincipal, baseAnnualRate, baseTermMonths);
        final double baseDti = FinancialMetricsCalculator.calculateDti(baseMonthlyPayment, baseAnnualIncome);

        // Build delta
        final SimulationDelta delta = new SimulationDelta();
        delta.setPdChange(getSafe(simulatedMetrics.getPd()) - getSafe(baseMetrics.getProbabilityOfDefault()));
        delta.setEclChange(getSafe(simulatedMetrics.getEcl()) - getSafe(baseMetrics.getExpectedCalculatedLoss()));
        final String baseRiskGradeName = baseMetrics.getRiskLevel() != null ? baseMetrics.getRiskLevel() : "UNKNOWN";
        delta.setRiskGradeChange(
                baseRiskGradeName + SimulationConstants.RISK_GRADE_ARROW + simulatedMetrics.getRiskGrade());
        delta.setMonthlyPaymentChange(getSafe(simulatedMetrics.getMonthlyPayment()) - baseMonthlyPayment);
        delta.setDtiChange(getSafe(simulatedMetrics.getDti()) - baseDti);
        
        return delta;
    }

    private double getDouble(final Map<String, Object> source, final String key, final double defaultValue) {
        if (source == null || !source.containsKey(key) || source.get(key) == null) {
            return defaultValue;
        }
        final Object value = source.get(key);
        if (value instanceof Number numberValue) {
            return numberValue.doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException ex) {
            throw new InvalidFormChangesException("Invalid numeric value for key: " + key);
        }
    }

    private double getSafe(final Double value) {
        return value == null ? SimulationConstants.ZERO_VALUE : value;
    }
}
