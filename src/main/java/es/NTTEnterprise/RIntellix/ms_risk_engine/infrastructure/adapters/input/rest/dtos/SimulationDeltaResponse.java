package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.input.rest.dtos;

public class SimulationDeltaResponse {
    private Double pdChange;
    private Double eclChange;
    private String riskGradeChange;
    private Double monthlyPaymentChange;
    private Double dtiChange;

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
}
