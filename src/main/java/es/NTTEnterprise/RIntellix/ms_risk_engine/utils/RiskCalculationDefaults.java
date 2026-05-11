package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RiskGrade;

/**
 * Centralized holder for risk calculation constants and shared utility methods.
 *
 * Contains all static default values used across admission and simulation
 * risk calculation logic, avoiding hardcoded magic numbers throughout the
 * codebase.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-25-2026
 */
public final class RiskCalculationDefaults {

    private RiskCalculationDefaults() {
        throw new UnsupportedOperationException(LogMessage.UTILITY_CLASS_NEVER_INSTANTIATE);
    }

    // ============================================================
    // LOAN DEFAULTS
    // ============================================================

    /** LGD for unsecured personal/consumer loans (European retail average). */
    public static final double LOAN_LGD = 0.70;

    // ============================================================
    // MORTGAGE DEFAULTS
    // ============================================================

    /** Market stress haircut applied to appraisal value. */
    public static final double MORTGAGE_HAIRCUT = 0.20;

    /** Foreclosure costs as a fraction of appraisal value (legal fees + taxes). */
    public static final double MORTGAGE_FORECLOSURE_COST_RATE = 0.10;

    /**
     * Regulatory floor — LGD cannot be lower than this regardless of collateral.
     */
    public static final double MORTGAGE_LGD_FLOOR = 0.10;

    /** LGD for unsecured loans when no collateral is provided. */
    public static final double MORTGAGE_LGD_UNSECURED_LOAN = 0.75;

    // ============================================================
    // CREDIT CARD — STANDARD DEFAULTS
    // ============================================================

    /** Credit Conversion Factor for standard credit cards (Basel Standard). */
    public static final double CC_STANDARD_CCF = 0.50;

    /**
     * LGD for standard credit cards (higher due to revolving balance difficulty).
     */
    public static final double CC_STANDARD_LGD = 0.80;

    // ============================================================
    // CREDIT CARD — REVOLVING DEFAULTS
    // ============================================================

    /** Credit Conversion Factor for revolving credit cards. */
    public static final double CC_REVOLVING_CCF = 0.75;

    /**
     * LGD for revolving credit cards (extremely low recovery rates in EU market).
     */
    public static final double CC_REVOLVING_LGD = 0.90;

    // ============================================================
    // ECL / DISCOUNT FACTOR
    // ============================================================

    /** Discount factor d = 1/(1+r)^t. For admission d = 1 (12-month projection). */
    public static final double DISCOUNT_FACTOR = 1.0;

    // ============================================================
    // RISK GRADE THRESHOLDS
    // ============================================================

    /** PD below this threshold maps to Grade A. */
    public static final double PD_THRESHOLD_GRADE_A = 0.05;

    /** PD below this threshold maps to Grade B. */
    public static final double PD_THRESHOLD_GRADE_B = 0.12;

    /** PD below this threshold maps to Grade C. */
    public static final double PD_THRESHOLD_GRADE_C = 0.22;

    /** ECL/EAD ratio below this triggers an upgrade in risk grade. */
    public static final double ECL_RATIO_UPGRADE_THRESHOLD = 0.02;

    /** ECL/EAD ratio above this triggers a downgrade in risk grade. */
    public static final double ECL_RATIO_DOWNGRADE_THRESHOLD = 0.08;

    /** Payment-to-income ratio above this triggers a grade downgrade. */
    public static final double PAYMENT_TO_INCOME_STRESS_THRESHOLD = 0.45;

    // ============================================================
    // SHARED UTILITY METHODS
    // ============================================================

    /**
     * Clamps a ratio value between 0.0 and 1.0.
     *
     * @param value the value to clamp.
     * @return the clamped value in range [0.0, 1.0].
     */
    public static double clampRatio(final double value) {
        if (value < 0.0) {
            return 0.0;
        }
        if (value > 1.0) {
            return 1.0;
        }
        return value;
    }

    /**
     * Validates the requested amount of the loan request.
     *
     * @param requestedAmount the requested amount.
     * @return the validated requested amount.
     * @throws IllegalArgumentException if the requested amount is null or less than
     *                                  or equal to 0.
     */
    public static double validateRequestAmount(Double requestedAmount) {
        if (requestedAmount == null || requestedAmount <= 0) {
            throw new IllegalArgumentException(LogMessage.REQUESTED_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
        }
        return requestedAmount;
    }

    // TODO: Check this ones.
    /**
     * Calculates the French (annuity) monthly payment for a fixed-rate loan.
     *
     * @param amount     the principal amount.
     * @param termMonths the number of monthly instalments.
     * @param annualRate the nominal annual interest rate (as percentage, e.g. 5.0
     *                   for 5%).
     * @return the monthly payment amount.
     */
    public static double calculateFrenchMonthlyPayment(final Double amount,
            final Integer termMonths, final Double annualRate) {
        double principal = validateRequestAmount(amount);
        int terms = termMonths == null || termMonths <= 0 ? 1 : termMonths;
        double monthlyRate = (annualRate == null ? 0.0 : annualRate) / 1200.0;

        if (monthlyRate == 0.0) {
            return principal / terms;
        }

        double numerator = principal * monthlyRate;
        double denominator = 1.0 - Math.pow(1.0 + monthlyRate, -terms);
        if (denominator == 0.0) {
            return principal / terms;
        }
        return numerator / denominator;
    }

    /**
     * Calculates the risk grade based on PD, ECL ratio, and payment-to-income.
     *
     * Applies a multi-factor scoring model:
     * 1. Base grade from PD thresholds (A/B/C/D).
     * 2. Adjustment from ECL-to-EAD ratio.
     * 3. Adjustment from payment-to-income stress indicator.
     *
     * @param pd           the probability of default.
     * @param ecl          the expected credit loss.
     * @param ead          the exposure at default.
     * @param amount       the requested amount for monthly payment calculation.
     * @param annualIncome the borrower annual income.
     * @param termMonths   the loan term in months (null for credit cards).
     * @param interestRate the annual interest rate.
     * @return the calculated risk grade.
     */
    public static RiskGrade calculateRiskGrade(final double pd, final double ecl, final double ead,
            final double amount, final Double annualIncome, final Integer termMonths,
            final Double interestRate) {

        int gradeIndex;
        if (pd < PD_THRESHOLD_GRADE_A) {
            gradeIndex = 0;
        } else if (pd < PD_THRESHOLD_GRADE_B) {
            gradeIndex = 1;
        } else if (pd < PD_THRESHOLD_GRADE_C) {
            gradeIndex = 2;
        } else {
            gradeIndex = 3;
        }

        double eclRatio = ead <= 0.0 ? 1.0 : (ecl / ead);
        if (eclRatio > ECL_RATIO_DOWNGRADE_THRESHOLD && gradeIndex < 3) {
            gradeIndex += 1;
        }
        if (eclRatio < ECL_RATIO_UPGRADE_THRESHOLD && gradeIndex > 0) {
            gradeIndex -= 1;
        }

        if (termMonths != null && termMonths > 0) {
            double monthlyPayment = calculateFrenchMonthlyPayment(amount, termMonths, interestRate);
            double income = annualIncome == null ? 0.0 : annualIncome;
            double monthlyIncome = income / 12.0;
            double paymentToIncome = monthlyIncome <= 0.0 ? 1.0 : monthlyPayment / monthlyIncome;
            if (paymentToIncome > PAYMENT_TO_INCOME_STRESS_THRESHOLD && gradeIndex < 3) {
                gradeIndex += 1;
            }
        }

        return switch (gradeIndex) {
            case 0 -> RiskGrade.A;
            case 1 -> RiskGrade.B;
            case 2 -> RiskGrade.C;
            default -> RiskGrade.D;
        };
    }
}
