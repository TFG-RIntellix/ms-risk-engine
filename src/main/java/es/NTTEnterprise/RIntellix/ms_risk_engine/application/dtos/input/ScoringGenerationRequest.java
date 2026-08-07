package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input;

/**
 * Data Transfer Object (DTO) for scoring generation input.
 * Contains all features required by the scoring engine model.
 * 
 * Features are extracted from domain entities (Request, Party, Contracts).
 * This DTO serves as the payload for publishing to Kafka scoring generation
 * events.
 * Field naming uses English convention for consistency with other DTOs in the
 * project.
 * 
 * @author Lucía Fernández Mancebo
 * @date 15/03/2026
 */
public class ScoringGenerationRequest extends ScoringGenerationPayload {

    private String partyId;

    // Socio-demographic features
    private Integer age;
    private String gender;
    private String maritalStatus;
    private String education;
    private Integer dependents;
    private String homeOwnership;
    private Boolean hasMortgage;

    // Employment features
    private String employmentStatus;
    private String occupationSector;

    // Financial features
    private Double annualIncome;
    private Double existingObligations;

    // Loan/Request features
    private String purpose;
    private String loanType;
    private Double loanAmount;
    private Integer termMonths;
    private Double interestRate;

    // Risk/Credit history features
    private Double ltv;
    private Double dti;
    private Integer previousLoansCount;
    private Integer previousDefaultsCount;

    /**
     * Default constructor for ScoringGenerationRequest.
     */
    public ScoringGenerationRequest() {
    }

    /**
     * Constructor for ScoringGenerationRequest.
     * 
     * @param requestId             the identifier of the request.
     * @param requestType           the type of the request.
     * @param partyId               the identifier of the party associated with the
     *                              request.
     * @param age                   the age of the party.
     * @param gender                the gender of the party.
     * @param maritalStatus         the marital status of the party.
     * @param education             the education level of the party.
     * @param dependents            the number of dependents of the party.
     * @param homeOwnership         the home ownership status of the party.
     * @param hasMortgage           whether the party has a mortgage or not.
     * @param employmentStatus      the employment status of the party.
     * @param occupationSector      the occupation sector of the party.
     * @param annualIncome          the annual income of the party.
     * @param purpose               the purpose of the loan/request.
     * @param loanAmount            the amount of the loan/request.
     * @param loanType              the type of the loan.
     * @param termMonths            the term of the loan/request in months.
     * @param interestRate          the interest rate of the loan/request.
     * @param ltv                   the loan-to-value ratio of the loan/request.
     * @param dti                   the debt-to-income ratio of the party.
     * @param previousLoansCount    the number of previous loans of the party.
     * @param previousDefaultsCount the number of previous defaults of the party.
     */
    public ScoringGenerationRequest(final String requestId, final String requestType, final String partyId,
            final Integer age, final String gender,
            final String maritalStatus, final String education, final Integer dependents,
            final String homeOwnership, final Boolean hasMortgage, final String employmentStatus,
            final String occupationSector, final Double annualIncome, final Double existingObligations, final String purpose,
            final Double loanAmount, String loanType, final Integer termMonths, final Double interestRate,
            final Double ltv, final Double dti, final Integer previousLoansCount,
            final Integer previousDefaultsCount) {
        super(requestId, requestType);
        this.partyId = partyId;
        this.age = age;
        this.gender = gender;
        this.maritalStatus = maritalStatus;
        this.education = education;
        this.dependents = dependents;
        this.homeOwnership = homeOwnership;
        this.hasMortgage = hasMortgage;
        this.employmentStatus = employmentStatus;
        this.occupationSector = occupationSector;
        this.annualIncome = annualIncome;
        this.existingObligations = existingObligations;
        this.purpose = purpose;
        this.loanAmount = loanAmount;
        this.termMonths = termMonths;
        this.interestRate = interestRate;
        this.ltv = ltv;
        this.dti = dti;
        this.previousLoansCount = previousLoansCount;
        this.previousDefaultsCount = previousDefaultsCount;
        this.loanType = loanType;
    }

    // Getters and setters.

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

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public Integer getDependents() {
        return dependents;
    }

    public void setDependents(Integer dependents) {
        this.dependents = dependents;
    }

    public String getHomeOwnership() {
        return homeOwnership;
    }

    public void setHomeOwnership(String homeOwnership) {
        this.homeOwnership = homeOwnership;
    }

    public Boolean getHasMortgage() {
        return hasMortgage;
    }

    public void setHasMortgage(Boolean hasMortgage) {
        this.hasMortgage = hasMortgage;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public String getOccupationSector() {
        return occupationSector;
    }

    public void setOccupationSector(String occupationSector) {
        this.occupationSector = occupationSector;
    }

    public Double getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(Double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public Double getExistingObligations() {
        return existingObligations;
    }

    public void setExistingObligations(Double existingObligations) {
        this.existingObligations = existingObligations;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Double getLtv() {
        return ltv;
    }

    public void setLtv(Double ltv) {
        this.ltv = ltv;
    }

    public Double getDti() {
        return dti;
    }

    public void setDti(Double dti) {
        this.dti = dti;
    }

    public Integer getPreviousLoansCount() {
        return previousLoansCount;
    }

    public void setPreviousLoansCount(Integer previousLoansCount) {
        this.previousLoansCount = previousLoansCount;
    }

    public Integer getPreviousDefaultsCount() {
        return previousDefaultsCount;
    }

    public void setPreviousDefaultsCount(Integer previousDefaultsCount) {
        this.previousDefaultsCount = previousDefaultsCount;
    }

    @Override
    public String toString() {
        return "ScoringGenerationDTO{" +
                ", partyId='" + partyId + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", maritalStatus='" + maritalStatus + '\'' +
                ", education='" + education + '\'' +
                ", dependents=" + dependents +
                ", homeOwnership='" + homeOwnership + '\'' +
                ", hasMortgage=" + hasMortgage +
                ", employmentStatus='" + employmentStatus + '\'' +
                ", occupationSector='" + occupationSector + '\'' +
                ", annualIncome=" + annualIncome +
                ", existingObligations=" + existingObligations +
                ", purpose='" + purpose + '\'' +
                ", loanAmount=" + loanAmount +
                ", termMonths=" + termMonths +
                ", interestRate=" + interestRate +
                ", ltv=" + ltv +
                ", dti=" + dti +
                ", previousLoansCount=" + previousLoansCount +
                ", previousDefaultsCount=" + previousDefaultsCount +
                '}';
    }
}
