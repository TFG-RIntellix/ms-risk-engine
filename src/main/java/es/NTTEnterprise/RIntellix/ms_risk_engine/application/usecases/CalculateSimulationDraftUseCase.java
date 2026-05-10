package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.output.FetchRequestPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.output.FetchScoringPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.output.PredictPdPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.BaseScoringData;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDraft;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RiskGrade;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.InvalidFormChangesException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.FinancialMetricsCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.RiskCalculationDefaults;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;

/**
 * Use case that orchestrates stateless simulation draft calculations.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
@Service
public class CalculateSimulationDraftUseCase {

    private static final String REQUESTED_AMOUNT_KEY = "requestedAmount";
    private static final String INTEREST_RATE_KEY = "interestRate";
    private static final String TERM_MONTHS_KEY = "termMonths";
    private static final String ANNUAL_INCOME_KEY = "annualIncome";

    private final FetchRequestPort fetchRequestPort;
    private final FetchScoringPort fetchScoringPort;
    private final PredictPdPort predictPdPort;

    public CalculateSimulationDraftUseCase(
            final FetchRequestPort fetchRequestPort,
            final FetchScoringPort fetchScoringPort,
            final PredictPdPort predictPdPort) {
        this.fetchRequestPort = Objects.requireNonNull(fetchRequestPort);
        this.fetchScoringPort = Objects.requireNonNull(fetchScoringPort);
        this.predictPdPort = Objects.requireNonNull(predictPdPort);
    }

    /**
     * Calculates a simulation draft without persistence.
     *
     * @param requestId   the request identifier.
     * @param formChanges the user-specified modified values.
     * @return the simulation draft with simulated metrics and deltas.
     */
    public SimulationDraft calculateDraft(final String requestId, final FormChanges formChanges) {
        if (requestId == null || requestId.isBlank()) {
            throw new InvalidFormChangesException("requestId is required");
        }
        if (formChanges == null) {
            throw new InvalidFormChangesException("formChanges is required");
        }

        final Map<String, Object> baseRequestData = fetchRequestPort.fetchByRequestId(requestId);
        final BaseScoringData baseScoring = fetchScoringPort.fetchByRequestId(requestId);
        if (baseScoring == null || baseScoring.getBaseMetrics() == null) {
            throw new ScoringNotFoundException("Scoring not found for requestId: " + requestId);
        }

        final Map<String, Object> baseVariables = mergeScenarioVariables(
                baseScoring.getInputSnapshot(),
                baseRequestData,
                Map.of());
        final Map<String, Object> mergedVariables = mergeScenarioVariables(
                baseVariables,
                Map.of(),
                formChanges.getValues());

        final Double predictedPd = predictPdPort.predictPd(mergedVariables, requestId);
        final SimulationMetrics simulated = buildSimulatedMetrics(predictedPd, mergedVariables, baseScoring.getBaseMetrics());
        final SimulationDelta delta = buildDelta(baseScoring.getBaseMetrics(), simulated, baseVariables);
        return new SimulationDraft(simulated, delta);
    }

    private Map<String, Object> mergeScenarioVariables(
            final Map<String, Object> inputSnapshot,
            final Map<String, Object> baseRequestData,
            final Map<String, Object> formChanges) {
        final Map<String, Object> merged = new HashMap<>();
        if (inputSnapshot != null) {
            merged.putAll(inputSnapshot);
        }
        if (baseRequestData != null) {
            merged.putAll(baseRequestData);
        }
        if (formChanges != null) {
            merged.putAll(formChanges);
        }
        return merged;
    }

    private SimulationMetrics buildSimulatedMetrics(
            final Double predictedPd,
            final Map<String, Object> mergedVariables,
            final SimulationMetrics baseMetrics) {

        final double principal = getDouble(mergedVariables, REQUESTED_AMOUNT_KEY,
                baseMetrics.getEad() == null ? SimulationConstants.ZERO_VALUE : baseMetrics.getEad());
        final double annualRate = getDouble(mergedVariables, INTEREST_RATE_KEY, SimulationConstants.ZERO_VALUE);
        final int termMonths = (int) getDouble(mergedVariables, TERM_MONTHS_KEY, SimulationConstants.MIN_TERM_MONTHS);
        final double annualIncome = getDouble(mergedVariables, ANNUAL_INCOME_KEY, SimulationConstants.ZERO_VALUE);

        final double monthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(principal, annualRate, termMonths);
        final double dti = FinancialMetricsCalculator.calculateDti(monthlyPayment, annualIncome);
        final double totalPayment = FinancialMetricsCalculator.calculateTotalPayment(monthlyPayment, termMonths);
        final double totalInterest = FinancialMetricsCalculator.calculateTotalInterest(totalPayment, principal);
        final double disposableIncome = FinancialMetricsCalculator.calculateDisposableIncome(annualIncome, monthlyPayment);

        final double pd = predictedPd == null ? SimulationConstants.ZERO_VALUE : predictedPd;
        final double lgd = baseMetrics.getLgd() == null ? RiskCalculationDefaults.LOAN_LGD : baseMetrics.getLgd();
        final double ead = principal;
        final double ecl = pd * lgd * ead * RiskCalculationDefaults.DISCOUNT_FACTOR;
        final RiskGrade riskGrade = RiskCalculationDefaults.calculateRiskGrade(
                pd,
                ecl,
                ead,
                principal,
                annualIncome,
                termMonths,
                annualRate);

        final SimulationMetrics metrics = new SimulationMetrics();
        metrics.setPd(pd);
        metrics.setLgd(lgd);
        metrics.setEad(ead);
        metrics.setEcl(ecl);
        metrics.setRiskGrade(riskGrade.name());
        metrics.setMonthlyPayment(monthlyPayment);
        metrics.setDti(dti);
        metrics.setTotalPayment(totalPayment);
        metrics.setTotalInterest(totalInterest);
        metrics.setDisposableIncome(disposableIncome);
        return metrics;
    }

    private SimulationDelta buildDelta(
            final SimulationMetrics baseMetrics,
            final SimulationMetrics simulatedMetrics,
            final Map<String, Object> baseVariables) {
        final double principal = getDouble(baseVariables, REQUESTED_AMOUNT_KEY,
                baseMetrics.getEad() == null ? SimulationConstants.ZERO_VALUE : baseMetrics.getEad());
        final double annualRate = getDouble(baseVariables, INTEREST_RATE_KEY, SimulationConstants.ZERO_VALUE);
        final int termMonths = (int) getDouble(baseVariables, TERM_MONTHS_KEY, SimulationConstants.MIN_TERM_MONTHS);
        final double annualIncome = getDouble(baseVariables, ANNUAL_INCOME_KEY, SimulationConstants.ZERO_VALUE);
        final double baseMonthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(principal, annualRate, termMonths);
        final double baseDti = FinancialMetricsCalculator.calculateDti(baseMonthlyPayment, annualIncome);

        final SimulationDelta delta = new SimulationDelta();
        delta.setPdChange(getSafe(simulatedMetrics.getPd()) - getSafe(baseMetrics.getPd()));
        delta.setEclChange(getSafe(simulatedMetrics.getEcl()) - getSafe(baseMetrics.getEcl()));
        delta.setRiskGradeChange(
                String.valueOf(baseMetrics.getRiskGrade()) + SimulationConstants.RISK_GRADE_ARROW + simulatedMetrics.getRiskGrade());
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
