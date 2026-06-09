package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data Transfer Object (DTO) for credit card scoring generation input.
 * Contains the credit-card-specific features required by the scoring engine.
 *
 * This DTO acts as the infrastructure-level representation of credit card
 * scoring messages received from Kafka. Unknown fields from the generic
 * ScoringGenerationDTO (e.g. education, occupationSector, hasMortgage,
 * purpose, loanAmount, termMonths, ltv, previousLoansCount) are safely
 * ignored during deserialization.
 *
 * @author Lucía Fernández Mancebo
 * @Date 06-09-2026
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditCardScoringGenerationDTO {

    private String requestId;
    private String requestType;
    private String partyId;

    // Socio-demographic features
    private Integer age;
    private String gender;
    private String maritalStatus;
    private String employmentStatus;
    private Double employmentSeniorityYears;
    private Integer dependents;

    // Financial features
    private Double annualIncome;
    private String incomeType;
    private String homeOwnership;

    // Credit card specific features
    private Double creditLimit;
    private Boolean isRevolving;
    private Double interestRate;
    private Double lti;
    private Double dti;
    private Integer previousDefaultsCount;

    public CreditCardScoringGenerationDTO() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public Double getEmploymentSeniorityYears() {
        return employmentSeniorityYears;
    }

    public void setEmploymentSeniorityYears(Double employmentSeniorityYears) {
        this.employmentSeniorityYears = employmentSeniorityYears;
    }

    public Integer getDependents() {
        return dependents;
    }

    public void setDependents(Integer dependents) {
        this.dependents = dependents;
    }

    public Double getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(Double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }

    public String getHomeOwnership() {
        return homeOwnership;
    }

    public void setHomeOwnership(String homeOwnership) {
        this.homeOwnership = homeOwnership;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Boolean getIsRevolving() {
        return isRevolving;
    }

    public void setIsRevolving(Boolean isRevolving) {
        this.isRevolving = isRevolving;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Double getLti() {
        return lti;
    }

    public void setLti(Double lti) {
        this.lti = lti;
    }

    public Double getDti() {
        return dti;
    }

    public void setDti(Double dti) {
        this.dti = dti;
    }

    public Integer getPreviousDefaultsCount() {
        return previousDefaultsCount;
    }

    public void setPreviousDefaultsCount(Integer previousDefaultsCount) {
        this.previousDefaultsCount = previousDefaultsCount;
    }

    @Override
    public String toString() {
        return "CreditCardScoringGenerationDTO{"
                + "requestId='" + requestId + '\''
                + ", requestType='" + requestType + '\''
                + ", partyId='" + partyId + '\''
                + ", age=" + age
                + ", gender='" + gender + '\''
                + ", maritalStatus='" + maritalStatus + '\''
                + ", employmentStatus='" + employmentStatus + '\''
                + ", employmentSeniorityYears=" + employmentSeniorityYears
                + ", dependents=" + dependents
                + ", annualIncome=" + annualIncome
                + ", incomeType='" + incomeType + '\''
                + ", homeOwnership='" + homeOwnership + '\''
                + ", creditLimit=" + creditLimit
                + ", isRevolving=" + isRevolving
                + ", interestRate=" + interestRate
                + ", lti=" + lti
                + ", dti=" + dti
                + ", previousDefaultsCount=" + previousDefaultsCount
                + '}';
    }
}
