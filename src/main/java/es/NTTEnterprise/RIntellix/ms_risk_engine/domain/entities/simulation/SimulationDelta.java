package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation;

import java.util.Objects;

/**
 * Domain entity that captures the delta between the base scoring
 * and the simulated metrics.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-11-2026
 */
public class SimulationDelta {

    private Double pdChange;
    private Double eclChange;
    private String riskGradeChange;
    private Double monthlyPaymentChange;
    private Double dtiChange;

    public SimulationDelta() {
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(pdChange, eclChange, riskGradeChange,
                monthlyPaymentChange, dtiChange);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SimulationDelta other = (SimulationDelta) obj;
        return Objects.equals(pdChange, other.pdChange)
                && Objects.equals(eclChange, other.eclChange)
                && Objects.equals(riskGradeChange, other.riskGradeChange)
                && Objects.equals(monthlyPaymentChange, other.monthlyPaymentChange)
                && Objects.equals(dtiChange, other.dtiChange);
    }

    @Override
    public String toString() {
        return "SimulationDelta{" +
                "pdChange=" + pdChange +
                ", eclChange=" + eclChange +
                ", riskGradeChange='" + riskGradeChange + '\'' +
                ", monthlyPaymentChange=" + monthlyPaymentChange +
                ", dtiChange=" + dtiChange +
                '}';
    }
}
