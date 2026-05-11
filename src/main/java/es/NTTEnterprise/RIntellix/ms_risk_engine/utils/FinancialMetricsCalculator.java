package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

/**
 * Utility class for financial metric calculations used in simulations.
 *
 * Provides standard calculations for monthly payment, DTI,
 * total payment, total interest, and disposable income.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-11-2026
 */
public final class FinancialMetricsCalculator {

    private FinancialMetricsCalculator() {
        throw new UnsupportedOperationException(LogMessage.UTILITY_CLASS_NEVER_INSTANTIATE);
    }

    /**
     * Calculates the monthly payment for a fixed-rate loan.
     *
     * @param principal   the loan amount.
     * @param annualRate  the annual interest rate.
     * @param termMonths  the term in months.
     * @return the monthly payment.
     */
    public static double calculateMonthlyPayment(final Double principal,
            final Double annualRate,
            final Integer termMonths) {
        if (principal == null || principal <= 0.0) {
            return SimulationConstants.ZERO_VALUE;
        }
        final int safeTerm = termMonths == null || termMonths <= 0
                ? SimulationConstants.MIN_TERM_MONTHS
                : termMonths;
        return RiskCalculationDefaults.calculateFrenchMonthlyPayment(
                principal,
                safeTerm,
                annualRate == null ? SimulationConstants.ZERO_VALUE : annualRate);
    }

    /**
     * Calculates the debt-to-income ratio.
     *
     * @param monthlyPayment the monthly payment amount.
     * @param annualIncome   the annual income.
     * @return the DTI ratio.
     */
    public static double calculateDti(final Double monthlyPayment, final Double annualIncome) {
        if (monthlyPayment == null || monthlyPayment <= 0.0) {
            return SimulationConstants.ZERO_VALUE;
        }
        if (annualIncome == null || annualIncome <= 0.0) {
            return SimulationConstants.ZERO_VALUE;
        }
        final double monthlyIncome = annualIncome / 12.0;
        return monthlyIncome <= 0.0 ? SimulationConstants.ZERO_VALUE : monthlyPayment / monthlyIncome;
    }

    /**
     * Calculates total payment across the term.
     *
     * @param monthlyPayment the monthly payment.
     * @param termMonths     the term in months.
     * @return the total payment.
     */
    public static double calculateTotalPayment(final Double monthlyPayment, final Integer termMonths) {
        if (monthlyPayment == null || termMonths == null || termMonths <= 0) {
            return SimulationConstants.ZERO_VALUE;
        }
        return monthlyPayment * termMonths;
    }

    /**
     * Calculates total interest across the term.
     *
     * @param totalPayment the total payment.
     * @param principal    the loan amount.
     * @return the total interest.
     */
    public static double calculateTotalInterest(final Double totalPayment, final Double principal) {
        if (totalPayment == null || principal == null || principal <= 0.0) {
            return SimulationConstants.ZERO_VALUE;
        }
        return totalPayment - principal;
    }

    /**
     * Calculates disposable income after monthly payment.
     *
     * @param annualIncome   the annual income.
     * @param monthlyPayment the monthly payment.
     * @return the disposable income.
     */
    public static double calculateDisposableIncome(final Double annualIncome, final Double monthlyPayment) {
        if (annualIncome == null || annualIncome <= 0.0) {
            return SimulationConstants.ZERO_VALUE;
        }
        final double monthlyIncome = annualIncome / 12.0;
        if (monthlyPayment == null) {
            return monthlyIncome;
        }
        return monthlyIncome - monthlyPayment;
    }
}
