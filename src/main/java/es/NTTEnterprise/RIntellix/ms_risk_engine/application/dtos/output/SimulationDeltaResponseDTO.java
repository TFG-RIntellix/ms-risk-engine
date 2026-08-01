package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output;

/**
 * DTO for representing the changes in risk metrics after a simulation.
 * Contains the deltas for PD, ECL, risk grade, monthly payment,
 * and DTI compared to the original scoring.
 * Used to be sent to the frontend after performing a simulation to show
 * the impact of the changes made in the input features on the risk assessment.
 *
 * @author Lucía Fernández Mancebo
 * @date 10/05/2026
 *
 */
public class SimulationDeltaResponseDTO {

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

    /**
     * Default constructor for SimulationDeltaResponseDTO.
     * 
     * @return A new instance of SimulationDeltaResponseDTO with default values.
     */
    public SimulationDeltaResponseDTO() {
    }

    /**
     * Constructor for SimulationDeltaResponseDTO.
     * 
     * @param pdChange             The change in Probability of Default (PD)
     *                             compared to the original scoring
     * @param eclChange            The change in Expected Credit Loss (ECL) compared
     *                             to the original scoring
     * @param riskGradeChange      The change in risk grade compared to the original
     *                             scoring (e.g., "Upgraded", "Downgraded",
     *                             "Unchanged")
     * @param monthlyPaymentChange The change in monthly payment compared to the
     *                             original scoring
     * @param dtiChange            The change in Debt-to-Income ratio (DTI) compared
     *                             to the original scoring
     */
    public SimulationDeltaResponseDTO(Double pdChange, Double lgdChange, Double eadChange, Double eclChange, String riskGradeChange,
            Double monthlyPaymentChange, Double dtiChange) {
        this.pdChange = pdChange;
        this.lgdChange = lgdChange;
        this.eadChange = eadChange;
        this.eclChange = eclChange;
        this.riskGradeChange = riskGradeChange;
        this.monthlyPaymentChange = monthlyPaymentChange;
        this.dtiChange = dtiChange;
    }

    // Getters and Setters
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

    // Equals, hashCode, and toString methods

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SimulationDeltaResponseDTO that = (SimulationDeltaResponseDTO) o;

        if (pdChange != null ? !pdChange.equals(that.pdChange) : that.pdChange != null)
            return false;
        if (lgdChange != null ? !lgdChange.equals(that.lgdChange) : that.lgdChange != null)
            return false;
        if (eadChange != null ? !eadChange.equals(that.eadChange) : that.eadChange != null)
            return false;
        if (eclChange != null ? !eclChange.equals(that.eclChange) : that.eclChange != null)
            return false;
        if (riskGradeChange != null ? !riskGradeChange.equals(that.riskGradeChange) : that.riskGradeChange != null)
            return false;
        if (monthlyPaymentChange != null ? !monthlyPaymentChange.equals(that.monthlyPaymentChange)
                : that.monthlyPaymentChange != null)
            return false;
        if (dtiChange != null ? !dtiChange.equals(that.dtiChange) : that.dtiChange != null)
            return false;
        if (totalPaymentChange != null ? !totalPaymentChange.equals(that.totalPaymentChange)
                : that.totalPaymentChange != null)
            return false;
        if (totalInterestChange != null ? !totalInterestChange.equals(that.totalInterestChange)
                : that.totalInterestChange != null)
            return false;
        return monthlyDisposableIncomeChange != null
                ? monthlyDisposableIncomeChange.equals(that.monthlyDisposableIncomeChange)
                : that.monthlyDisposableIncomeChange == null;
    }

    @Override
    public int hashCode() {
        int result = pdChange != null ? pdChange.hashCode() : 0;
        result = 31 * result + (lgdChange != null ? lgdChange.hashCode() : 0);
        result = 31 * result + (eadChange != null ? eadChange.hashCode() : 0);
        result = 31 * result + (eclChange != null ? eclChange.hashCode() : 0);
        result = 31 * result + (riskGradeChange != null ? riskGradeChange.hashCode() : 0);
        result = 31 * result + (monthlyPaymentChange != null ? monthlyPaymentChange.hashCode() : 0);
        result = 31 * result + (dtiChange != null ? dtiChange.hashCode() : 0);
        result = 31 * result + (totalPaymentChange != null ? totalPaymentChange.hashCode() : 0);
        result = 31 * result + (totalInterestChange != null ? totalInterestChange.hashCode() : 0);
        result = 31 * result + (monthlyDisposableIncomeChange != null ? monthlyDisposableIncomeChange.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SimulationDeltaResponseDTO{" +
                "pdChange=" + pdChange +
                ", lgdChange=" + lgdChange +
                ", eadChange=" + eadChange +
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
