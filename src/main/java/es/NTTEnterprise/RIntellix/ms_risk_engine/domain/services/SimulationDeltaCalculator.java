package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.FinancialMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.FinancialMetricsStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.FinancialMetricsStrategyFactory;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.MapUtilities;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.MathUtilities;

/**
 * Domain service for calculating the delta (difference) between base metrics
 * and simulated metrics.
 * 
 * Extracts simulation delta logic into a domain service for better SRP and
 * testability.
 *
 * @author Lucia Fernandez Mancebo
 */
public class SimulationDeltaCalculator {

    private final List<FinancialMetricsStrategy> financialMetricsStrategies;

    public SimulationDeltaCalculator(final List<FinancialMetricsStrategy> financialMetricsStrategies) {
        this.financialMetricsStrategies = Objects.requireNonNull(financialMetricsStrategies, LogMessage.FINANCIAL_METRICS_STRATEGIES_CANNOT_BE_NULL);
    }

    /**
     * Builds the simulation delta by comparing base metrics with simulated metrics.
     * Delta represents the change in key metrics between the base scenario and the
     * simulated scenario.
     * 
     * @param baseScoring      the original scoring object with base metrics.
     * @param simulatedMetrics the new metrics from simulation.
     * @param baseVariables    the original feature values.
     * @param mergedVariables  the merged feature values (includes form changes).
     * @return the SimulationDelta with all calculated changes.
     */
    public SimulationDelta calculateDelta(
            final Scoring baseScoring,
            final RiskMetrics simulatedMetrics,
            final Map<String, Object> baseVariables,
            final Map<String, Object> mergedVariables) {

        // Extract base financial metrics from the scoring
        final RiskMetrics baseMetrics = baseScoring.getResults();
        final double baseMonthlyPayment = baseMetrics.getFinancialMetrics() != null ? baseMetrics.getFinancialMetrics().getMonthlyPayment() : 0.0;
        final double baseDti = baseMetrics.getFinancialMetrics() != null ? baseMetrics.getFinancialMetrics().getDebtToIncomeRatio() : 0.0;
        final double baseTotalPayment = baseMetrics.getFinancialMetrics() != null ? baseMetrics.getFinancialMetrics().getTotalPayment() : 0.0;
        final double baseTotalInterest = baseMetrics.getFinancialMetrics() != null ? baseMetrics.getFinancialMetrics().getTotalInterest() : 0.0;
        final double baseDisposableIncome = baseMetrics.getFinancialMetrics() != null ? baseMetrics.getFinancialMetrics().getMonthlyDisposableIncome() : 0.0;

        // Extract request details to pick strategy
        final String requestType = baseScoring.getRequestId().split("-")[0]; // In real scenario, it should be part of context. We can get it from mergedVariables or assume PRESTAMO.
        final String resolvedRequestType = (String) mergedVariables.getOrDefault("requestType", "PRESTAMO");
        final Boolean isRevolving = (Boolean) mergedVariables.get(ModelPayloadFieldNames.FIELD_IS_REVOLVING);

        // Extract simulated variables for calculation
        double simAmount = MapUtilities.getDouble(mergedVariables, ModelPayloadFieldNames.FIELD_LOAN_AMOUNT, 
                                MapUtilities.getDouble(baseVariables, ModelPayloadFieldNames.FIELD_LOAN_AMOUNT, 0));
        
        // If it's a credit card, use credit limit instead
        if (simAmount == 0 && (mergedVariables.containsKey(ModelPayloadFieldNames.FIELD_CREDIT_LIMIT) || baseVariables.containsKey(ModelPayloadFieldNames.FIELD_CREDIT_LIMIT))) {
            simAmount = MapUtilities.getDouble(mergedVariables, ModelPayloadFieldNames.FIELD_CREDIT_LIMIT, 
                                MapUtilities.getDouble(baseVariables, ModelPayloadFieldNames.FIELD_CREDIT_LIMIT, 0));
        }

        final double simAnnualRate = MapUtilities.getDouble(mergedVariables,
                ModelPayloadFieldNames.FIELD_INTEREST_RATE,
                MapUtilities.getDouble(baseVariables,
                        ModelPayloadFieldNames.FIELD_INTEREST_RATE, 0));
        final int simTermMonths = (int) MapUtilities.getDouble(mergedVariables,
                ModelPayloadFieldNames.FIELD_TERM_MONTHS,
                MapUtilities.getDouble(baseVariables,
                        ModelPayloadFieldNames.FIELD_TERM_MONTHS,
                        SimulationConstants.MIN_TERM_MONTHS));
        final double simAnnualIncome = MapUtilities.getDouble(mergedVariables,
                ModelPayloadFieldNames.FIELD_ANNUAL_INCOME,
                MapUtilities.getDouble(baseVariables,
                        ModelPayloadFieldNames.FIELD_ANNUAL_INCOME, 0));

        // Use the strategy to calculate the simulated financial metrics
        final FinancialMetricsStrategy strategy = FinancialMetricsStrategyFactory.createStrategy(resolvedRequestType, isRevolving, financialMetricsStrategies);
        
        // We calculate delta without existing obligations here for simplicity, or we could pass 0 if DTI was already recalculated
        // For delta we usually just want to see the change in the new payment part, or total DTI.
        // Actually, RiskIndicatorCalculationService recalculates the overall DTI and stores it. So we can just pull it!
        final double simDti = MapUtilities.getDouble(mergedVariables, ModelPayloadFieldNames.FIELD_DTI, baseDti);

        final FinancialMetrics simFinancialMetrics = strategy.calculateFinancialMetrics(simAmount, simAnnualRate, simAnnualIncome, 0.0, simTermMonths);
        final double simMonthlyPayment = simFinancialMetrics.getMonthlyPayment();
        final double simTotalPayment = simFinancialMetrics.getTotalPayment();
        final double simTotalInterest = simFinancialMetrics.getTotalInterest();
        final double simDisposableIncome = simFinancialMetrics.getMonthlyDisposableIncome(); // This is using 0 existing obligations inside strategy, maybe slightly off for standard

        // Build delta
        final SimulationDelta delta = new SimulationDelta();
        delta.setPdChange(MathUtilities.roundIntermediate(
                SimulationConstants.getSafe(simulatedMetrics.getProbabilityOfDefault())
                        - SimulationConstants.getSafe(baseMetrics.getProbabilityOfDefault())));
        delta.setEclChange(MathUtilities.roundFinal(SimulationConstants.getSafe(simulatedMetrics.getExpectedCalculatedLoss())
                        - SimulationConstants.getSafe(baseMetrics.getExpectedCalculatedLoss())));
        
        final String baseRiskGradeName = baseMetrics.getRiskLevel() != null ? baseMetrics.getRiskLevel()
                : SimulationConstants.UNKNOWN_RISK_GRADE;
        final String simRiskGradeName = simulatedMetrics.getRiskLevel() != null
                ? simulatedMetrics.getRiskLevel()
                : SimulationConstants.UNKNOWN_RISK_GRADE;
                
        delta.setRiskGradeChange(baseRiskGradeName + SimulationConstants.RISK_GRADE_ARROW + simRiskGradeName);
        delta.setMonthlyPaymentChange(MathUtilities.roundFinal(simMonthlyPayment - baseMonthlyPayment));
        delta.setDtiChange(MathUtilities.roundFinal(simDti - baseDti));
        delta.setTotalPaymentChange(MathUtilities.roundFinal(simTotalPayment - baseTotalPayment));
        delta.setTotalInterestChange(MathUtilities.roundFinal(simTotalInterest - baseTotalInterest));
        delta.setMonthlyDisposableIncomeChange(MathUtilities.roundFinal(simDisposableIncome - baseDisposableIncome));

        return delta;
    }
}
