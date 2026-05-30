package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import java.util.Map;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.FinancialMetricsCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.MapUtilities;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.MathUtilities;

/**
 * Domain service for calculating the delta (difference) between base metrics
 * and simulated metrics.
 * 
 * Extracts simulation delta logic into a domain service for better SRP and
 * testability.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 05-30-2026
 */
public class SimulationDeltaCalculator {

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
        final double baseMonthlyPayment = baseMetrics.getFinancialMetrics().getMonthlyPayment();
        final double baseDti = baseMetrics.getFinancialMetrics().getDebtToIncomeRatio();
        final double baseTotalPayment = baseMetrics.getFinancialMetrics().getTotalPayment();
        final double baseTotalInterest = baseMetrics.getFinancialMetrics().getTotalInterest();
        final double baseDisposableIncome = baseMetrics.getFinancialMetrics().getMonthlyDisposableIncome();

        // Extract simulated variables for calculation
        final double simPrincipal = MapUtilities.getDouble(mergedVariables,
                ModelPayloadFieldNames.FIELD_LOAN_AMOUNT,
                MapUtilities.getDouble(baseVariables,
                        ModelPayloadFieldNames.FIELD_LOAN_AMOUNT, 0));
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

        // Calculate simulated financial metrics for comparison
        final double simMonthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(simPrincipal,
                simAnnualRate, simTermMonths);
        final double simDti = FinancialMetricsCalculator.calculateDti(simMonthlyPayment, simAnnualIncome);
        final double simTotalPayment = FinancialMetricsCalculator.calculateTotalPayment(simMonthlyPayment,
                simTermMonths);
        final double simTotalInterest = FinancialMetricsCalculator.calculateTotalInterest(simTotalPayment,
                simPrincipal);
        final double simDisposableIncome = FinancialMetricsCalculator.calculateDisposableIncome(
                simMonthlyPayment, simAnnualIncome);

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
