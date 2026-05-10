package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities;

import java.util.Objects;

/**
 * Represents simulation form fields that are allowed to be modified.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class FormChanges {

    private Double interestRate;
    private Integer termMonths;
    private Double requestedAmount;
    private Double annualIncome;
    private String employmentStatus;
    private Boolean hasMortgage;

    /**
     * Constructor of the FormChanges class.
     */
    public FormChanges() {
    }

    /**
     * Constructor of the FormChanges class.
     *
     * @param interestRate     the annual interest rate.
     * @param termMonths       the loan term in months.
     * @param requestedAmount  the requested amount.
     * @param annualIncome     the annual income.
     * @param employmentStatus the employment status.
     * @param hasMortgage      indicates if applicant has mortgage.
     */
    public FormChanges(final Double interestRate,
            final Integer termMonths,
            final Double requestedAmount,
            final Double annualIncome,
            final String employmentStatus,
            final Boolean hasMortgage) {
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.requestedAmount = requestedAmount;
        this.annualIncome = annualIncome;
        this.employmentStatus = employmentStatus;
        this.hasMortgage = hasMortgage;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(final Double interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(final Integer termMonths) {
        this.termMonths = termMonths;
    }

    public Double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(final Double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Double getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(final Double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(final String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public Boolean getHasMortgage() {
        return hasMortgage;
    }

    public void setHasMortgage(final Boolean hasMortgage) {
        this.hasMortgage = hasMortgage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(interestRate);
        result = prime * result + Objects.hashCode(termMonths);
        result = prime * result + Objects.hashCode(requestedAmount);
        result = prime * result + Objects.hashCode(annualIncome);
        result = prime * result + Objects.hashCode(employmentStatus);
        result = prime * result + Objects.hashCode(hasMortgage);
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
        final FormChanges other = (FormChanges) obj;
        return Objects.equals(interestRate, other.interestRate)
                && Objects.equals(termMonths, other.termMonths)
                && Objects.equals(requestedAmount, other.requestedAmount)
                && Objects.equals(annualIncome, other.annualIncome)
                && Objects.equals(employmentStatus, other.employmentStatus)
                && Objects.equals(hasMortgage, other.hasMortgage);
    }

    @Override
    public String toString() {
        return "FormChanges{" +
                "interestRate=" + interestRate +
                ", termMonths=" + termMonths +
                ", requestedAmount=" + requestedAmount +
                ", annualIncome=" + annualIncome +
                ", employmentStatus='" + employmentStatus + '\'' +
                ", hasMortgage=" + hasMortgage +
                '}';
    }
}
