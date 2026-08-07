package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common;

/**
 * Value object representing financial affordability metrics for a credit
 * product.
 * 
 * These metrics provide business and customer insights into payment obligations
 * and disposable income capacity.
 * 
 * Metrics:
 * - Monthly Payment: The periodic payment required to repay the credit facility
 * - Debt-to-Income Ratio (DTI): Total monthly obligations / monthly income
 * - Total Payment: Total amount paid over the life of the credit product
 * - Total Interest: Interest cost during the lifetime of the credit product
 * - Monthly Disposable Income: Income remaining after obligations and new
 * payment
 * 
 * @author Lucía Fernández Mancebo
 * @date 26/05/2026
 */
public class FinancialMetrics {

    private Double monthlyPayment;
    private Double debtToIncomeRatio;
    private Double totalPayment;
    private Double totalInterest;
    private Double monthlyDisposableIncome;

    /**
     * Default constructor for FinancialMetrics.
     */
    public FinancialMetrics() {
    }

    /**
     * Parameterized constructor for FinancialMetrics.
     *
     * @param monthlyPayment          the periodic payment amount
     * @param debtToIncomeRatio       the DTI ratio (0-1 range)
     * @param totalPayment            the total amount paid over the term
     * @param totalInterest           the total interest cost
     * @param monthlyDisposableIncome the income remaining after obligations
     */
    public FinancialMetrics(
            final Double monthlyPayment,
            final Double debtToIncomeRatio,
            final Double totalPayment,
            final Double totalInterest,
            final Double monthlyDisposableIncome) {
        this.monthlyPayment = monthlyPayment;
        this.debtToIncomeRatio = debtToIncomeRatio;
        this.totalPayment = totalPayment;
        this.totalInterest = totalInterest;
        this.monthlyDisposableIncome = monthlyDisposableIncome;
    }

    // Getters and Setters

    public Double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(final Double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public Double getDebtToIncomeRatio() {
        return debtToIncomeRatio;
    }

    public void setDebtToIncomeRatio(final Double debtToIncomeRatio) {
        this.debtToIncomeRatio = debtToIncomeRatio;
    }

    public Double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(final Double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public Double getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(final Double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public Double getMonthlyDisposableIncome() {
        return monthlyDisposableIncome;
    }

    public void setMonthlyDisposableIncome(final Double monthlyDisposableIncome) {
        this.monthlyDisposableIncome = monthlyDisposableIncome;
    }

    @Override
    public String toString() {
        return "FinancialMetrics{" +
                "monthlyPayment=" + monthlyPayment +
                ", debtToIncomeRatio=" + debtToIncomeRatio +
                ", totalPayment=" + totalPayment +
                ", totalInterest=" + totalInterest +
                ", monthlyDisposableIncome=" + monthlyDisposableIncome +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((monthlyPayment == null) ? 0 : monthlyPayment.hashCode());
        result = prime * result + ((debtToIncomeRatio == null) ? 0 : debtToIncomeRatio.hashCode());
        result = prime * result + ((totalPayment == null) ? 0 : totalPayment.hashCode());
        result = prime * result + ((totalInterest == null) ? 0 : totalInterest.hashCode());
        result = prime * result + ((monthlyDisposableIncome == null) ? 0 : monthlyDisposableIncome.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FinancialMetrics other = (FinancialMetrics) obj;
        if (monthlyPayment == null) {
            if (other.monthlyPayment != null)
                return false;
        } else if (!monthlyPayment.equals(other.monthlyPayment))
            return false;
        if (debtToIncomeRatio == null) {
            if (other.debtToIncomeRatio != null)
                return false;
        } else if (!debtToIncomeRatio.equals(other.debtToIncomeRatio))
            return false;
        if (totalPayment == null) {
            if (other.totalPayment != null)
                return false;
        } else if (!totalPayment.equals(other.totalPayment))
            return false;
        if (totalInterest == null) {
            if (other.totalInterest != null)
                return false;
        } else if (!totalInterest.equals(other.totalInterest))
            return false;
        if (monthlyDisposableIncome == null) {
            if (other.monthlyDisposableIncome != null)
                return false;
        } else if (!monthlyDisposableIncome.equals(other.monthlyDisposableIncome))
            return false;
        return true;
    }
}
