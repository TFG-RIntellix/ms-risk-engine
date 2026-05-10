package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CalculateSimulationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.CalculateSimulationResponse;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.SimulationMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RiskGrade;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.SimulationNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.SimulationValidationException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.SimulationRepositoryPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.RiskCalculationDefaults;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationCalculationDefaults;

/**
 * Use case for simulation recalculation.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
@Service
public class CalculateSimulationUseCase {

    private static final String MODEL_KEY_INTEREST_RATE = "tasa_interes";
    private static final String MODEL_KEY_TERM_MONTHS = "plazo_meses";
    private static final String MODEL_KEY_REQUESTED_AMOUNT = "monto_prestamo";
    private static final String MODEL_KEY_ANNUAL_INCOME = "ingresos_anuales";
    private static final String MODEL_KEY_EMPLOYMENT_STATUS = "situacion_laboral";
    private static final String MODEL_KEY_HAS_MORTGAGE = "tiene_hipoteca";
    private static final String YES_VALUE = "Si";
    private static final String NO_VALUE = "No";

    private final SimulationRepositoryPort simulationRepositoryPort;
    private final ScoringModelInvocationService modelInvocationService;
    private final String predictLoanPath;

    /**
     * Constructor of the CalculateSimulationUseCase class.
     *
     * @param simulationRepositoryPort simulation repository output port.
     * @param modelInvocationService   model invocation service.
     * @param predictLoanPath          prediction endpoint path.
     */
    public CalculateSimulationUseCase(
            final SimulationRepositoryPort simulationRepositoryPort,
            final ScoringModelInvocationService modelInvocationService,
            @Value("${risk.model.predict-loan-path:/api/v1/risk/predict-loan}") final String predictLoanPath) {
        this.simulationRepositoryPort = Objects.requireNonNull(simulationRepositoryPort);
        this.modelInvocationService = Objects.requireNonNull(modelInvocationService);
        this.predictLoanPath = Objects.requireNonNull(predictLoanPath);
    }

    /**
     * Calculates simulation metrics and deltas for modified form values.
     *
     * @param request calculate request.
     * @return calculated simulation response.
     */
    public CalculateSimulationResponse calculateSimulation(final CalculateSimulationRequest request) {
        if (request == null || request.getRequestId() == null || request.getRequestId().isBlank()) {
            throw new SimulationValidationException(LogMessage.SIMULATION_REQUEST_ID_REQUIRED);
        }

        if (request.getFormChanges() == null) {
            throw new SimulationValidationException(LogMessage.SIMULATION_FORM_CHANGES_REQUIRED);
        }

        final Scoring baseScoring = simulationRepositoryPort.fetchBaseScoringByRequestId(request.getRequestId());
        if (baseScoring == null || baseScoring.getResults() == null || baseScoring.getInputSnapshot() == null) {
            throw new SimulationNotFoundException(LogMessage.SIMULATION_BASE_SCORING_NOT_FOUND);
        }

        final Map<String, Object> simulationModelPayload = mergeSimulationFormChanges(
                baseScoring.getInputSnapshot(),
                request.getFormChanges());

        final CompletableFuture<ModelPredictionResult> modelResultFuture = modelInvocationService.invokePrediction(
                simulationModelPayload,
                request.getRequestId(),
                predictLoanPath);

        final ModelPredictionResult predictionResult = modelResultFuture.join();
        final SimulationMetrics simulatedMetrics = buildSimulatedMetrics(baseScoring.getResults(), simulationModelPayload,
                predictionResult);
        final SimulationMetrics baseFinancialMetrics = buildSimulationFinancialMetrics(baseScoring.getResults(),
                baseScoring.getInputSnapshot(),
                baseScoring.getResults().getRiskLevel());

        final SimulationDelta delta = buildSimulationDelta(baseScoring.getResults(), simulatedMetrics, baseFinancialMetrics);

        return new CalculateSimulationResponse(simulatedMetrics, delta);
    }

    private SimulationMetrics buildSimulatedMetrics(final RiskMetrics baseMetrics,
            final Map<String, Object> simulationModelPayload,
            final ModelPredictionResult predictionResult) {

        final Double pd = predictionResult == null ? null : predictionResult.getProbabilityOfDefault();
        final double safePd = pd == null ? SimulationCalculationDefaults.ZERO_DOUBLE : pd;
        final double lgd = baseMetrics.getLossGivenDefault() == null
                ? RiskCalculationDefaults.LOAN_LGD
                : baseMetrics.getLossGivenDefault();

        final double requestedAmount = readDouble(simulationModelPayload.get(MODEL_KEY_REQUESTED_AMOUNT));
        final double annualIncome = readDouble(simulationModelPayload.get(MODEL_KEY_ANNUAL_INCOME));
        final int termMonths = readInteger(simulationModelPayload.get(MODEL_KEY_TERM_MONTHS));
        final double interestRate = readDouble(simulationModelPayload.get(MODEL_KEY_INTEREST_RATE));

        final double ead = requestedAmount > SimulationCalculationDefaults.ZERO_DOUBLE
                ? requestedAmount
                : defaultDouble(baseMetrics.getExposureAtDefault());
        final double ecl = safePd * lgd * ead * RiskCalculationDefaults.DISCOUNT_FACTOR;

        final RiskGrade riskGrade = RiskCalculationDefaults.calculateRiskGrade(
                safePd,
                ecl,
                ead,
                requestedAmount,
                annualIncome,
                termMonths,
                interestRate);

        return buildSimulationFinancialMetrics(
                new RiskMetrics(safePd, lgd, ead, ecl, riskGrade),
                simulationModelPayload,
                riskGrade == null ? null : riskGrade.name());
    }

    private SimulationMetrics buildSimulationFinancialMetrics(final RiskMetrics riskMetrics,
            final Map<String, Object> modelPayload,
            final String riskGrade) {

        final double requestedAmount = readDouble(modelPayload.get(MODEL_KEY_REQUESTED_AMOUNT));
        final double annualIncome = readDouble(modelPayload.get(MODEL_KEY_ANNUAL_INCOME));
        final int termMonths = readInteger(modelPayload.get(MODEL_KEY_TERM_MONTHS));
        final double interestRate = readDouble(modelPayload.get(MODEL_KEY_INTEREST_RATE));

        final double monthlyPayment = SimulationCalculationDefaults.calculateMonthlyPayment(
                requestedAmount,
                interestRate,
                termMonths);
        final double dti = SimulationCalculationDefaults.calculateDti(monthlyPayment, annualIncome);
        final double totalPayment = SimulationCalculationDefaults.calculateTotalPayment(monthlyPayment, termMonths);
        final double totalInterest = SimulationCalculationDefaults.calculateTotalInterest(totalPayment, requestedAmount);
        final double disposableIncome = SimulationCalculationDefaults.calculateDisposableIncome(annualIncome, monthlyPayment);

        final SimulationMetrics simulatedMetrics = new SimulationMetrics();
        simulatedMetrics.setPd(riskMetrics.getProbabilityOfDefault());
        simulatedMetrics.setLgd(riskMetrics.getLossGivenDefault());
        simulatedMetrics.setEad(riskMetrics.getExposureAtDefault());
        simulatedMetrics.setEcl(riskMetrics.getExpectedCalculatedLoss());
        simulatedMetrics.setRiskGrade(riskGrade);
        simulatedMetrics.setMonthlyPayment(monthlyPayment);
        simulatedMetrics.setDti(dti);
        simulatedMetrics.setTotalPayment(totalPayment);
        simulatedMetrics.setTotalInterest(totalInterest);
        simulatedMetrics.setDisposableIncome(disposableIncome);

        return simulatedMetrics;
    }

    private SimulationDelta buildSimulationDelta(final RiskMetrics baseRiskMetrics,
            final SimulationMetrics simulatedMetrics,
            final SimulationMetrics baseFinancialMetrics) {
        final SimulationDelta delta = new SimulationDelta();
        final double basePd = defaultDouble(baseRiskMetrics.getProbabilityOfDefault());
        final double baseEcl = defaultDouble(baseRiskMetrics.getExpectedCalculatedLoss());
        final double baseMonthlyPayment = defaultDouble(baseFinancialMetrics.getMonthlyPayment());
        final double baseDti = defaultDouble(baseFinancialMetrics.getDti());

        delta.setPdChange(defaultDouble(simulatedMetrics.getPd()) - basePd);
        delta.setEcaChange(defaultDouble(simulatedMetrics.getEcl()) - baseEcl);
        delta.setRiskGradeChange(buildRiskGradeDelta(baseRiskMetrics.getRiskLevel(), simulatedMetrics.getRiskGrade()));
        delta.setMonthlyPaymentChange(defaultDouble(simulatedMetrics.getMonthlyPayment()) - baseMonthlyPayment);
        delta.setDtiChange(defaultDouble(simulatedMetrics.getDti()) - baseDti);
        return delta;
    }

    private String buildRiskGradeDelta(final String baseRiskGrade, final String simulatedRiskGrade) {
        final String leftValue = baseRiskGrade == null ? "N/A" : baseRiskGrade;
        final String rightValue = simulatedRiskGrade == null ? "N/A" : simulatedRiskGrade;

        if (leftValue.equals(rightValue)) {
            return leftValue;
        }
        return leftValue + " -> " + rightValue;
    }

    private Map<String, Object> mergeSimulationFormChanges(final Map<String, Object> baseInputSnapshot,
            final FormChanges formChanges) {
        final Map<String, Object> mergedPayload = new LinkedHashMap<>(baseInputSnapshot);

        if (formChanges.getInterestRate() != null) {
            mergedPayload.put(MODEL_KEY_INTEREST_RATE, formChanges.getInterestRate());
        }
        if (formChanges.getTermMonths() != null) {
            mergedPayload.put(MODEL_KEY_TERM_MONTHS, formChanges.getTermMonths());
        }
        if (formChanges.getRequestedAmount() != null) {
            mergedPayload.put(MODEL_KEY_REQUESTED_AMOUNT, formChanges.getRequestedAmount());
        }
        if (formChanges.getAnnualIncome() != null) {
            mergedPayload.put(MODEL_KEY_ANNUAL_INCOME, formChanges.getAnnualIncome());
        }
        if (formChanges.getEmploymentStatus() != null) {
            mergedPayload.put(MODEL_KEY_EMPLOYMENT_STATUS, formChanges.getEmploymentStatus());
        }
        if (formChanges.getHasMortgage() != null) {
            mergedPayload.put(MODEL_KEY_HAS_MORTGAGE, Boolean.TRUE.equals(formChanges.getHasMortgage()) ? YES_VALUE : NO_VALUE);
        }

        return mergedPayload;
    }

    private static double defaultDouble(final Double value) {
        return value == null ? SimulationCalculationDefaults.ZERO_DOUBLE : value;
    }

    private static double readDouble(final Object value) {
        if (value == null) {
            return SimulationCalculationDefaults.ZERO_DOUBLE;
        }
        if (value instanceof Number numberValue) {
            return numberValue.doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }

    private static int readInteger(final Object value) {
        if (value == null) {
            return SimulationCalculationDefaults.MIN_TERM_MONTHS;
        }
        if (value instanceof Number numberValue) {
            return numberValue.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
}
