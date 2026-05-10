package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities;

import java.util.Objects;

/**
 * Represents metric differences between base and simulated scenarios.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class SimulationDelta {

    private Double pdChange;
    private Double ecaChange;
    private String riskGradeChange;

    private Double monthlyPaymentChange;
    private Double dtiChange;

    /**
     * Constructor of the SimulationDelta class.
     */
    public SimulationDelta() {
    }

    public Double getPdChange() {
        return pdChange;
    }

    public void setPdChange(final Double pdChange) {
        this.pdChange = pdChange;
    }

    public Double getEcaChange() {
        return ecaChange;
    }

    public void setEcaChange(final Double ecaChange) {
        this.ecaChange = ecaChange;
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
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(pdChange);
        result = prime * result + Objects.hashCode(ecaChange);
        result = prime * result + Objects.hashCode(riskGradeChange);
        result = prime * result + Objects.hashCode(monthlyPaymentChange);
        result = prime * result + Objects.hashCode(dtiChange);
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
        final SimulationDelta other = (SimulationDelta) obj;
        return Objects.equals(pdChange, other.pdChange)
                && Objects.equals(ecaChange, other.ecaChange)
                && Objects.equals(riskGradeChange, other.riskGradeChange)
                && Objects.equals(monthlyPaymentChange, other.monthlyPaymentChange)
                && Objects.equals(dtiChange, other.dtiChange);
    }

    @Override
    public String toString() {
        return "SimulationDelta{" +
                "pdChange=" + pdChange +
                ", ecaChange=" + ecaChange +
                ", riskGradeChange='" + riskGradeChange + '\'' +
                ", monthlyPaymentChange=" + monthlyPaymentChange +
                ", dtiChange=" + dtiChange +
                '}';
    }
}
