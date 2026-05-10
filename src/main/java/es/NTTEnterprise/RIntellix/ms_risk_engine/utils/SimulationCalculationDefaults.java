package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

/**
 * Centralized constants and formulas for simulation financial metrics.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public final class SimulationCalculationDefaults {

    public static final double ZERO_DOUBLE = 0.0;
    public static final double HUNDRED_PERCENT = 100.0;
    public static final int MONTHS_PER_YEAR = 12;
    public static final int MIN_TERM_MONTHS = 1;

    private SimulationCalculationDefaults() {
        throw new UnsupportedOperationException(LogMessage.UTILITY_CLASS_NEVER_INSTANTIATE);
    }

    /**
     * Calculates monthly payment using French amortization formula.
     *
     * @param principal       the requested principal.
     * @param annualRate      the annual nominal rate in percentage.
     * @param termMonths      the term in months.
     * @return monthly payment.
     */
    public static double calculateMonthlyPayment(final double principal, final double annualRate, final int termMonths) {
        if (principal <= ZERO_DOUBLE) {
            return ZERO_DOUBLE;
        }

        final int safeTermMonths = termMonths <= ZERO_DOUBLE ? MIN_TERM_MONTHS : termMonths;
        final double monthlyRate = annualRate / HUNDRED_PERCENT / MONTHS_PER_YEAR;

        if (monthlyRate == ZERO_DOUBLE) {
            return principal / safeTermMonths;
        }

        final double monthlyRateFactor = Math.pow(1 + monthlyRate, safeTermMonths);
        final double numerator = principal * (monthlyRate * monthlyRateFactor);
        final double denominator = monthlyRateFactor - 1;

        if (denominator == ZERO_DOUBLE) {
            return principal / safeTermMonths;
        }

        return numerator / denominator;
    }

    /**
     * Calculates debt-to-income ratio as percentage.
     *
     * @param monthlyPayment  monthly payment.
     * @param annualIncome    annual income.
     * @return dti in percentage.
     */
    public static double calculateDti(final double monthlyPayment, final double annualIncome) {
        if (annualIncome <= ZERO_DOUBLE) {
            return HUNDRED_PERCENT;
        }

        final double monthlyIncome = annualIncome / MONTHS_PER_YEAR;
        if (monthlyIncome <= ZERO_DOUBLE) {
            return HUNDRED_PERCENT;
        }

        return (monthlyPayment / monthlyIncome) * HUNDRED_PERCENT;
    }

    /**
     * Calculates total payment amount.
     *
     * @param monthlyPayment monthly payment.
     * @param termMonths     term in months.
     * @return total payment.
     */
    public static double calculateTotalPayment(final double monthlyPayment, final int termMonths) {
        final int safeTermMonths = termMonths <= ZERO_DOUBLE ? MIN_TERM_MONTHS : termMonths;
        return monthlyPayment * safeTermMonths;
    }

    /**
     * Calculates total interest paid.
     *
     * @param totalPayment total payment amount.
     * @param principal    principal amount.
     * @return total interest.
     */
    public static double calculateTotalInterest(final double totalPayment, final double principal) {
        return totalPayment - principal;
    }

    /**
     * Calculates disposable monthly income.
     *
     * @param annualIncome   annual income.
     * @param monthlyPayment monthly payment.
     * @return disposable income.
     */
    public static double calculateDisposableIncome(final double annualIncome, final double monthlyPayment) {
        return (annualIncome / MONTHS_PER_YEAR) - monthlyPayment;
    }
}
