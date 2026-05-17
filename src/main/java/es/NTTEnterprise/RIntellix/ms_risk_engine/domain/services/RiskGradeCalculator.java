package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import java.util.Objects;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.services.LoanPaymentCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RiskGrade;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.RiskCalculationDefaults;

/**
 * Domain service for calculating risk grades based on multiple risk factors.
 * This service encapsulates the business logic for risk grade determination
 * using a multi-factor scoring model.
 *
 * Responsibilities:
 * - Classify probability of default into risk grades (A, B, C, D)
 * - Adjust grades based on ECL-to-EAD ratio
 * - Adjust grades based on payment-to-income ratio stress
 * - Apply Basel III compliance rules
 *
 * Risk Grade Algorithm:
 * 1. Base grade from PD thresholds (A/B/C/D)
 * 2. Downgrade if ECL-to-EAD ratio exceeds threshold
 * 3. Upgrade if ECL-to-EAD ratio is very low
 * 4. Adjust further based on payment-to-income ratio for term loans
 * // TODO: Tocheck calculations.
 * 
 * @author Lucia Fernandez Mancebo
 * @Date 09-05-2026
 */
@Component
public class RiskGradeCalculator {

    private final LoanPaymentCalculator paymentCalculator;

    public RiskGradeCalculator(final LoanPaymentCalculator paymentCalculator) {
        this.paymentCalculator = Objects.requireNonNull(paymentCalculator,
                LogMessage.LOAN_PAYMENT_CALCULATOR_CANNOT_BE_NULL);
    }

    /**
     * Calculate risk grade based on probability of default and other risk factors.
     * Applies a multi-factor scoring model that considers PD, ECL-to-EAD ratio,
     * and payment-to-income stress.
     *
     * @param pd           the probability of default (0.0 to 1.0).
     * @param ecl          the expected credit loss.
     * @param ead          the exposure at default.
     * @param amount       the loan/credit amount for monthly payment calculation.
     * @param annualIncome the borrower's annual income.
     * @param termMonths   the loan term in months (null for credit cards).
     * @param interestRate the annual interest rate.
     * @return the calculated risk grade (A, B, C, or D).
     * @throws NullPointerException if pd is null.
     */
    public RiskGrade calculateRiskGrade(
            final double pd,
            final double ecl,
            final double ead,
            final double amount,
            final Double annualIncome,
            final Integer termMonths,
            final Double interestRate) {

        Objects.requireNonNull(pd, LogMessage.PROBABILITY_OF_DEFAULT_CANNOT_BE_NULL);

        // Step 1: Determine base grade from PD thresholds
        int gradeIndex = getBaseGradeIndex(pd);

        // Step 2: Adjust grade based on ECL-to-EAD ratio
        gradeIndex = adjustGradeByEclRatio(gradeIndex, ecl, ead);

        // Step 3: Adjust grade based on payment-to-income ratio (for term loans)
        if (termMonths != null && termMonths > 0) {
            gradeIndex = adjustGradeByPaymentToIncomeRatio(
                    gradeIndex,
                    amount,
                    annualIncome,
                    termMonths,
                    interestRate);
        }

        // Convert index to RiskGrade enum
        return switch (gradeIndex) {
            case 0 -> RiskGrade.A;
            case 1 -> RiskGrade.B;
            case 2 -> RiskGrade.C;
            default -> RiskGrade.D;
        };
    }

    /**
     * Determines the base risk grade index (0-3) from PD thresholds.
     * Grade A: PD < 5% | Grade B: 5% <= PD < 10% | Grade C: 10% <= PD < 20% |
     * Grade D: PD >= 20%
     *
     * @param pd the probability of default.
     * @return the grade index (0=A, 1=B, 2=C, 3=D).
     */
    private int getBaseGradeIndex(final double pd) {
        if (pd < RiskCalculationDefaults.PD_THRESHOLD_GRADE_A) {
            return 0;
        } else if (pd < RiskCalculationDefaults.PD_THRESHOLD_GRADE_B) {
            return 1;
        } else if (pd < RiskCalculationDefaults.PD_THRESHOLD_GRADE_C) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Adjusts grade based on ECL-to-EAD ratio.
     * Higher ratios indicate higher relative losses, triggering downgrades.
     * Lower ratios may trigger upgrades.
     *
     * @param currentGradeIndex the current grade index.
     * @param ecl               the expected credit loss.
     * @param ead               the exposure at default.
     * @return the adjusted grade index.
     */
    private int adjustGradeByEclRatio(final int currentGradeIndex, final double ecl, final double ead) {
        int adjusted = currentGradeIndex;
        final double eclRatio = ead <= 0.0 ? 1.0 : (ecl / ead);

        // Downgrade if ratio is high
        if (eclRatio > RiskCalculationDefaults.ECL_RATIO_DOWNGRADE_THRESHOLD && adjusted < 3) {
            adjusted += 1;
        }

        // Upgrade if ratio is very low
        if (eclRatio < RiskCalculationDefaults.ECL_RATIO_UPGRADE_THRESHOLD && adjusted > 0) {
            adjusted -= 1;
        }

        return adjusted;
    }

    /**
     * Adjusts grade based on payment-to-income stress ratio.
     * High payment-to-income ratios indicate financial stress, triggering
     * downgrades.
     *
     * @param currentGradeIndex the current grade index.
     * @param amount            the loan amount.
     * @param annualIncome      the borrower's annual income.
     * @param termMonths        the loan term in months.
     * @param interestRate      the annual interest rate.
     * @return the adjusted grade index.
     */
    private int adjustGradeByPaymentToIncomeRatio(
            final int currentGradeIndex,
            final double amount,
            final Double annualIncome,
            final Integer termMonths,
            final Double interestRate) {

        // Calculate monthly payment using French amortization
        final double monthlyPayment = paymentCalculator.calculateFrenchMonthlyPayment(
                amount,
                termMonths,
                interestRate);

        // Calculate payment-to-income ratio
        final double annualIncomeNorm = annualIncome == null ? 0.0 : annualIncome;
        final double monthlyIncome = annualIncomeNorm / 12.0;
        final double paymentToIncomeRatio = monthlyIncome <= 0.0 ? 1.0 : monthlyPayment / monthlyIncome;

        // Downgrade if payment-to-income exceeds stress threshold
        if (paymentToIncomeRatio > RiskCalculationDefaults.PAYMENT_TO_INCOME_STRESS_THRESHOLD
                && currentGradeIndex < 3) {
            return currentGradeIndex + 1;
        }

        return currentGradeIndex;
    }
}
