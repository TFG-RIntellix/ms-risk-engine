package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.input.rest.dtos;

/**
 * Core component: SimulationDeltaResponse.
 * Encapsulates the logic and responsibilities assigned to this element
 * within the Hexagonal Architecture, ensuring separation of concerns.
 *
 * @author Lucía Fernández Mancebo
 * @date 28/07/2026
 */
public class SimulationDeltaResponse {
    private Double pdChange;
    private Double lgdChange;
    private Double eadChange;
    private Double eclChange;
    private String riskGradeChange;
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

    public Double getLgdChange() {
        return lgdChange;
    }

    public void setLgdChange(final Double lgdChange) {
        this.lgdChange = lgdChange;
    }

    public Double getEadChange() {
        return eadChange;
    }

    public void setEadChange(final Double eadChange) {
        this.eadChange = eadChange;
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
}
