package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input;

/**
 * Data Transfer Object (DTO) for credit card scoring generation.
 * Contains a subset of features required by the scoring engine model,
 * tailored specifically for credit card requests.
 *
 * Unlike standard loan/mortgage requests, credit card scoring focuses on:
 * - Socio-demographic data (age, gender, marital status, employment, income)
 * - Credit card-specific parameters (credit limit, revolving status)
 * - Basic request details (type, purpose)
 *
 * This specialized DTO reduces the payload size and ensures only relevant
 * data is sent to the scoring engine for credit cards, following the
 * Strategy Pattern for type-specific message transport.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-20-2026
 */
public class CreditCardScoringGenerationRequest extends ScoringGenerationPayload {

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
    private String requestType;
    private Double creditLimit;
    private Boolean isRevolving;
    private Double interestRate;
    private Double lti;
    private Double dti;
    private Integer previousDefaultsCount;

    /**
     * Default constructor for CreditCardScoringGenerationRequest.
     * 
     */
    public CreditCardScoringGenerationRequest() {
        super();
    }

    /**
     * Constructor for CreditCardScoringGenerationRequest.
     * 
     * @param requestId        the identifier of the request.
     * @param requestType      the type of the request.
     * @param partyId          the identifier of the party associated with the
     *                         request.
     * @param age              the age of the party.
     * @param gender           the gender of the party.
     * @param maritalStatus    the marital status of the party.
     * @param employmentStatus the employment status of the party.
     * @param annualIncome     the annual income of the party.
     * @param creditLimit      the credit limit of the requested credit card.
     * @param isRevolving      indicates if the credit card is revolving.
     * @return the constructed CreditCardScoringGenerationRequest instance.
     */
    public CreditCardScoringGenerationRequest(
            final String requestId, final String requestType,
            final String partyId,
            final Integer age,
            final String gender,
            final String maritalStatus,
            final String employmentStatus,
            final Double annualIncome,
            final Double creditLimit,
            final Boolean isRevolving) {
        super(requestId, requestType);
        this.partyId = partyId;
        this.age = age;
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        this.employmentStatus = employmentStatus;
        this.annualIncome = annualIncome;
        this.creditLimit = creditLimit;
        this.isRevolving = isRevolving;
    }

    // Getters and setters.
    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(final String partyId) {
        this.partyId = partyId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(final Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(final String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(final String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(final String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public Double getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(final Double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(final Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Boolean getIsRevolving() {
        return isRevolving;
    }

    public void setIsRevolving(final Boolean isRevolving) {
        this.isRevolving = isRevolving;
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

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
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
        return "CreditCardScoringGenerationRequest [partyId=" + partyId + ", age=" + age + ", gender=" + gender
                + ", maritalStatus=" + maritalStatus + ", employmentStatus=" + employmentStatus
                + ", employmentSeniorityYears=" + employmentSeniorityYears + ", dependents=" + dependents
                + ", annualIncome=" + annualIncome + ", incomeType=" + incomeType + ", homeOwnership=" + homeOwnership
                + ", requestType=" + requestType + ", creditLimit=" + creditLimit + ", isRevolving=" + isRevolving
                + ", interestRate=" + interestRate + ", lti=" + lti + ", dti=" + dti + ", previousDefaultsCount="
                + previousDefaultsCount + "]";
    }

}