package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import java.util.Map;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.FinancialMetricsCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.MathUtilities;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;

/**
 * Domain service for DTI calculations and existing obligation derivation.
 *
 * Encapsulates shared DTI-related logic used across scoring and simulation
 * workflows to keep mappers and orchestrators focused on single
 * responsibilities.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-30-2026
 */
public class DtiCalculationService {

    /**
     * Calculates the model DTI for scoring requests by adding existing DTI and
     * the new-loan DTI derived from the request's loan parameters.
     *
     * @param annualIncome     annual income used for DTI normalization.
     * @param existingDtiRatio existing DTI ratio from the request payload.
     * @param loanAmount       loan principal for the new request.
     * @param interestRate     annual nominal interest rate (percentage).
     * @param termMonths       loan term in months.
     * @return the model DTI ratio to send to the AI model.
     */
    public double calculateModelDtiForScoring(
            final double annualIncome,
            final double existingDtiRatio,
            final double loanAmount,
            final double interestRate,
            final Integer termMonths) {
        if (annualIncome <= SimulationConstants.ZERO_VALUE) {
            return SimulationConstants.ZERO_VALUE;
        }

        final int safeTermMonths = termMonths == null ? SimulationConstants.MIN_TERM_MONTHS : termMonths;
        final double monthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(
                loanAmount,
                interestRate,
                safeTermMonths);
        final double newLoanDtiRatio = FinancialMetricsCalculator.calculateDti(monthlyPayment, annualIncome);
        return MathUtilities.roundFinal(existingDtiRatio + newLoanDtiRatio);
    }

    /**
     * Calculates DTI using existing monthly obligations plus a new monthly payment.
     *
     * @param monthlyPayment      the new monthly payment.
     * @param annualIncome        the annual income used for normalization.
     * @param existingObligations existing monthly obligations.
     * @return the recalculated DTI ratio.
     */
    public double calculateDtiWithExistingObligations(
            final double monthlyPayment,
            final double annualIncome,
            final double existingObligations) {
        final double monthlyIncome = annualIncome / SimulationConstants.MONTHS_PER_YEAR;
        if (monthlyIncome <= SimulationConstants.ZERO_VALUE) {
            return SimulationConstants.ZERO_VALUE;
        }
        final double totalMonthlyObligations = existingObligations + monthlyPayment;
        return totalMonthlyObligations / monthlyIncome;
    }

    /**
     * Resolves existing monthly obligations from the base scoring input snapshot.
     *
     * @param baseInputSnapshot the base scoring input snapshot.
     * @return the existing monthly obligations, never negative.
     */
    public double resolveExistingMonthlyObligations(final Map<String, Object> baseInputSnapshot) {
        if (baseInputSnapshot == null || baseInputSnapshot.isEmpty()) {
            return SimulationConstants.ZERO_VALUE;
        }

        final double baseAnnualIncome = getDouble(baseInputSnapshot, ModelPayloadFieldNames.FIELD_ANNUAL_INCOME,
                SimulationConstants.ZERO_VALUE);
        final double baseMonthlyIncome = baseAnnualIncome / SimulationConstants.MONTHS_PER_YEAR;
        if (baseMonthlyIncome <= SimulationConstants.ZERO_VALUE) {
            return SimulationConstants.ZERO_VALUE;
        }

        final double baseLoanAmount = getDouble(baseInputSnapshot, ModelPayloadFieldNames.FIELD_LOAN_AMOUNT,
                SimulationConstants.ZERO_VALUE);
        final double baseInterestRate = getDouble(baseInputSnapshot, ModelPayloadFieldNames.FIELD_INTEREST_RATE,
                SimulationConstants.ZERO_VALUE);
        final int baseTermMonths = (int) getDouble(baseInputSnapshot, ModelPayloadFieldNames.FIELD_TERM_MONTHS,
                SimulationConstants.MIN_TERM_MONTHS);
        final double baseMonthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(
                baseLoanAmount, baseInterestRate, baseTermMonths);

        final double baseDti = getDouble(baseInputSnapshot, ModelPayloadFieldNames.FIELD_DTI,
                SimulationConstants.ZERO_VALUE);
        final double baseTotalMonthlyObligations = baseDti * baseMonthlyIncome;
        final double existingObligations = baseTotalMonthlyObligations - baseMonthlyPayment;
        return Math.max(existingObligations, SimulationConstants.ZERO_VALUE);
    }

    private double getDouble(final Map<String, Object> map, final String key, final double defaultValue) {
        if (map == null || !map.containsKey(key)) {
            return defaultValue;
        }
        final Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
}
