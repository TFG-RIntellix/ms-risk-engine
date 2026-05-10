package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities;

import java.util.Objects;

/**
 * Represents simulation output metrics.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class SimulationMetrics {

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

    /**
     * Constructor of the SimulationMetrics class.
     */
    public SimulationMetrics() {
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(pd);
        result = prime * result + Objects.hashCode(lgd);
        result = prime * result + Objects.hashCode(ead);
        result = prime * result + Objects.hashCode(ecl);
        result = prime * result + Objects.hashCode(riskGrade);
        result = prime * result + Objects.hashCode(monthlyPayment);
        result = prime * result + Objects.hashCode(dti);
        result = prime * result + Objects.hashCode(totalPayment);
        result = prime * result + Objects.hashCode(totalInterest);
        result = prime * result + Objects.hashCode(disposableIncome);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SimulationMetrics other = (SimulationMetrics) obj;
        return Objects.equals(pd, other.pd)
                && Objects.equals(lgd, other.lgd)
                && Objects.equals(ead, other.ead)
                && Objects.equals(ecl, other.ecl)
                && Objects.equals(riskGrade, other.riskGrade)
                && Objects.equals(monthlyPayment, other.monthlyPayment)
                && Objects.equals(dti, other.dti)
                && Objects.equals(totalPayment, other.totalPayment)
                && Objects.equals(totalInterest, other.totalInterest)
                && Objects.equals(disposableIncome, other.disposableIncome);
    }

    @Override
    public String toString() {
        return "SimulationMetrics{" +
                "pd=" + pd +
                ", lgd=" + lgd +
                ", ead=" + ead +
                ", ecl=" + ecl +
                ", riskGrade='" + riskGrade + '\'' +
                ", monthlyPayment=" + monthlyPayment +
                ", dti=" + dti +
                ", totalPayment=" + totalPayment +
                ", totalInterest=" + totalInterest +
                ", disposableIncome=" + disposableIncome +
                '}';
    }
}
