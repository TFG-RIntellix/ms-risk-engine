package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.constants;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;

/**
 * Centralized holder for risk calculation constants and shared utility methods.
 *
 * Contains all static default values used across admission and simulation
 * risk calculation logic, avoiding hardcoded magic numbers throughout the
 * codebase.
 *
 * @author Lucía Fernández Mancebo
 * @date 25/04/2026
 */
public final class RiskCalculationDefaults {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws UnsupportedOperationException if called, enforcing
     * non-instantiability.
     * This class is intended to be used in a static context only.
     * 
     * @throws UnsupportedOperationException always, to prevent instantiation.
     */
    private RiskCalculationDefaults() {
        throw new UnsupportedOperationException(LogMessage.UTILITY_CLASS_NEVER_INSTANTIATE);
    }

    // ============================================================
    // HARD-CUTOFF DEFAULTS
    // ============================================================

    /** DTI > 50 % triggers automatic rejection for all request types. */
    public static final double HARD_CUTOFF_DTI_THRESHOLD = 0.55;

    /** LTV > 80 % triggers automatic rejection for mortgage requests. */
    public static final double HARD_CUTOFF_LTV_THRESHOLD = 0.80;

    /** LTI > 40 % triggers automatic rejection for credit-card requests. */
    public static final double HARD_CUTOFF_LTI_THRESHOLD = 0.40;

    /** Risk grade assigned for hard-cutoff rejection. */
    public static final String HARD_CUTOFF_RISK_GRADE = "HIGH";

    /** Basel II standard unsecured LGD (loans, credit cards without collateral). */
    public static final double HARD_CUTOFF_UNSECURED_LGD = 0.45;

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
    public static final double CC_STANDARD_CCF = 0.30;

    /**
     * LGD for standard credit cards (higher due to revolving balance difficulty).
     */
    public static final double CC_STANDARD_LGD = 0.60;

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
    // CREDIT CARD — FINANCIAL METRICS CONSTANTS
    // ============================================================
    public static final double CC_DTI_PAYMENT_PERCENTAGE = 0.04;
    public static final double CC_FIXED_COSTS_INCOME_PERCENTAGE = 0.50;
    public static final double CC_AVERAGE_UTILIZATION_RATE = 0.50;
    public static final double CC_AVERAGE_PAYMENT_PERCENTAGE = 0.10;
    public static final double CC_MINIMUM_ABSOLUTE_PAYMENT = 25.0;
    public static final int CC_MAX_SIMULATION_MONTHS = 48;

    // ============================================================
    // ECL / DISCOUNT FACTOR
    // ============================================================

    /** Discount factor d = 1/(1+r)^t. For admission d = 1 (12-month projection). */
    public static final double DISCOUNT_FACTOR = 1.0;

    // ============================================================
    // RISK GRADE THRESHOLDS
    // Based on Basilea Principles of having different differentiate riskGrades
    // Taking account that PD of 20% is a high risk (1 of 5 borrowers defaults).
    // ============================================================

    /** PD below this threshold maps to Grade A. */
    public static final double PD_THRESHOLD_GRADE_A = 0.001;

    /** PD below this threshold maps to Grade B. */
    public static final double PD_THRESHOLD_GRADE_B = 0.005;

    /** PD below this threshold maps to Grade C. */
    public static final double PD_THRESHOLD_GRADE_C = 0.02;

    /** PD below this threshold maps to Grade D. */
    public static final double PD_THRESHOLD_GRADE_D = 0.05;

    /** PD below this threshold maps to Grade E. */
    public static final double PD_THRESHOLD_GRADE_E = 0.10;

    /** PD below this threshold maps to Grade F. */
    public static final double PD_THRESHOLD_GRADE_F = 0.20;

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

}
