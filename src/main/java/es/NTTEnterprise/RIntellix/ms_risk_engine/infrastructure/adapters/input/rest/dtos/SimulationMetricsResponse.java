package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.input.rest.dtos;

public class SimulationMetricsResponse {
    private Double pd;
    private Double lgd;
    private Double ead;
    private Double ecl;
    private String riskGrade;
    private Double monthlyPayment;
    private Double dti;
    private Double totalPayment;
    private Double totalInterest;
    private Double disposableIncome;

    public Double getPd() {
        return pd;
    }

    public void setPd(final Double pd) {
        this.pd = pd;
    }

    public Double getLgd() {
        return lgd;
    }

    public void setLgd(final Double lgd) {
        this.lgd = lgd;
    }

    public Double getEad() {
        return ead;
    }

    public void setEad(final Double ead) {
        this.ead = ead;
    }

    public Double getEcl() {
        return ecl;
    }

    public void setEcl(final Double ecl) {
        this.ecl = ecl;
    }

    public String getRiskGrade() {
        return riskGrade;
    }

    public void setRiskGrade(final String riskGrade) {
        this.riskGrade = riskGrade;
    }

    public Double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(final Double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public Double getDti() {
        return dti;
    }

    public void setDti(final Double dti) {
        this.dti = dti;
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

    public Double getDisposableIncome() {
        return disposableIncome;
    }

    public void setDisposableIncome(final Double disposableIncome) {
        this.disposableIncome = disposableIncome;
    }
}
