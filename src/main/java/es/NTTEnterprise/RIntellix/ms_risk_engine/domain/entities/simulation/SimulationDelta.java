package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation;

/**
 * Value object representing variation versus base scenario.
 * 
 * Tracks changes in both risk metrics and financial metrics between
 * the original scoring and a what-if simulation scenario.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 * @Updated 05-26-2026 - Added financial metrics delta tracking
 */
public class SimulationDelta {
    private Double pdChange;
    private Double eclChange;
    private String riskGradeChange;

    // Financial metrics deltas
    private Double monthlyPaymentChange;
    private Double dtiChange;
    private Double totalPaymentChange;
    private Double totalInterestChange;
    private Double monthlyDisposableIncomeChange;

    public Double getPdChange() {
        return pdChange;
    }

    public void setPdChange(final Double pdChange) {
        this.pdChange = pdChange;
    }

    public Double getEclChange() {
        return eclChange;
    }

    public void setEclChange(final Double eclChange) {
        this.eclChange = eclChange;
    }

    public String getRiskGradeChange() {
        return riskGradeChange;
    }

    public void setRiskGradeChange(final String riskGradeChange) {
        this.riskGradeChange = riskGradeChange;
    }

    public Double getMonthlyPaymentChange() {
        return monthlyPaymentChange;
    }

    public void setMonthlyPaymentChange(final Double monthlyPaymentChange) {
        this.monthlyPaymentChange = monthlyPaymentChange;
    }

    public Double getDtiChange() {
        return dtiChange;
    }

    public void setDtiChange(final Double dtiChange) {
        this.dtiChange = dtiChange;
    }

    public Double getTotalPaymentChange() {
        return totalPaymentChange;
    }

    public void setTotalPaymentChange(final Double totalPaymentChange) {
        this.totalPaymentChange = totalPaymentChange;
    }

    public Double getTotalInterestChange() {
        return totalInterestChange;
    }

    public void setTotalInterestChange(final Double totalInterestChange) {
        this.totalInterestChange = totalInterestChange;
    }

    public Double getMonthlyDisposableIncomeChange() {
        return monthlyDisposableIncomeChange;
    }

    public void setMonthlyDisposableIncomeChange(final Double monthlyDisposableIncomeChange) {
        this.monthlyDisposableIncomeChange = monthlyDisposableIncomeChange;
    }

    @Override
    public String toString() {
        return "SimulationDelta{" +
                "pdChange=" + pdChange +
                ", eclChange=" + eclChange +
                ", riskGradeChange='" + riskGradeChange + '\'' +
                ", monthlyPaymentChange=" + monthlyPaymentChange +
                ", dtiChange=" + dtiChange +
                ", totalPaymentChange=" + totalPaymentChange +
                ", totalInterestChange=" + totalInterestChange +
                ", monthlyDisposableIncomeChange=" + monthlyDisposableIncomeChange +
                '}';
    }
}
