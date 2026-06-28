package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import java.util.Map;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.FinancialMetricsCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.utils.MapUtilities;
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
            final double existingObligationsAnnual,
            final double loanAmount,
            final double interestRate,
            final Integer termMonths) {
        if (annualIncome <= SimulationConstants.ZERO_VALUE) {
            return SimulationConstants.ZERO_VALUE;
        }

        final double existingMonthly = existingObligationsAnnual / SimulationConstants.MONTHS_PER_YEAR;
        final int safeTermMonths = termMonths == null ? SimulationConstants.MIN_TERM_MONTHS : termMonths;
        final double monthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(
                loanAmount,
                interestRate,
                safeTermMonths);
        final double monthlyIncome = annualIncome / SimulationConstants.MONTHS_PER_YEAR;
        return MathUtilities.roundFinal((existingMonthly + monthlyPayment) / monthlyIncome);
    }

    /**
     * Calculates the model DTI for credit card scoring requests by adding existing
     * DTI
     * and the new credit card DTI derived from the request parameters.
     *
     * @param annualIncome              annual income used for DTI normalization.
     * @param existingObligationsAnnual existing annual obligations.
     * @param creditLimit               credit limit for the new card.
     * @param isRevolving               whether the credit card is revolving.
     * @return the model DTI ratio to send to the AI model.
     */
    public double calculateModelDtiForCreditCardScoring(
            final double annualIncome,
            final double existingObligationsAnnual,
            final double creditLimit,
            final Boolean isRevolving) {
        if (annualIncome <= SimulationConstants.ZERO_VALUE) {
            return SimulationConstants.ZERO_VALUE;
        }

        final double existingMonthly = existingObligationsAnnual / SimulationConstants.MONTHS_PER_YEAR;
        final double monthlyPayment = CreditCardFinancialMetricsCalculator.calculateMonthlyPayment(creditLimit, isRevolving);

        final double monthlyIncome = annualIncome / SimulationConstants.MONTHS_PER_YEAR;
        return MathUtilities.roundFinal((existingMonthly + monthlyPayment) / monthlyIncome);
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

        final double baseAnnualIncome = MapUtilities.getDouble(baseInputSnapshot, ModelPayloadFieldNames.FIELD_ANNUAL_INCOME,
                SimulationConstants.ZERO_VALUE);
        final double baseMonthlyIncome = baseAnnualIncome / SimulationConstants.MONTHS_PER_YEAR;
        if (baseMonthlyIncome <= SimulationConstants.ZERO_VALUE) {
            return SimulationConstants.ZERO_VALUE;
        }

        final double baseLoanAmount = MapUtilities.getDouble(baseInputSnapshot, ModelPayloadFieldNames.FIELD_LOAN_AMOUNT,
                SimulationConstants.ZERO_VALUE);
        final double baseCreditLimit = MapUtilities.getDouble(baseInputSnapshot, ModelPayloadFieldNames.FIELD_CREDIT_LIMIT,
                SimulationConstants.ZERO_VALUE);

        double baseMonthlyPayment = 0.0;

        // If it's a loan/mortgage
        if (baseLoanAmount > 0) {
            final double baseInterestRate = MapUtilities.getDouble(baseInputSnapshot, ModelPayloadFieldNames.FIELD_INTEREST_RATE,
                    SimulationConstants.ZERO_VALUE);
            final int baseTermMonths = (int) MapUtilities.getDouble(baseInputSnapshot, ModelPayloadFieldNames.FIELD_TERM_MONTHS,
                    SimulationConstants.MIN_TERM_MONTHS);
            baseMonthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(
                    baseLoanAmount, baseInterestRate, baseTermMonths);
        }
        // If it's a credit card
        else if (baseCreditLimit > 0) {
            final Boolean isRevolving = (Boolean) baseInputSnapshot.get(ModelPayloadFieldNames.FIELD_IS_REVOLVING);
            baseMonthlyPayment = CreditCardFinancialMetricsCalculator.calculateMonthlyPayment(baseCreditLimit, isRevolving);
        }

        final double baseDti = MapUtilities.getDouble(baseInputSnapshot, ModelPayloadFieldNames.FIELD_DTI,
                SimulationConstants.ZERO_VALUE);
        final double baseTotalMonthlyObligations = baseDti * baseMonthlyIncome;
        final double existingObligations = baseTotalMonthlyObligations - baseMonthlyPayment;
        return Math.max(existingObligations, SimulationConstants.ZERO_VALUE);
    }
}
