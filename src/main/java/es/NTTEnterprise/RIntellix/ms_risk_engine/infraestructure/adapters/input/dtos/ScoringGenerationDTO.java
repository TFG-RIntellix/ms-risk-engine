package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.dtos;

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
 * @Date 03-15-2026
 */
public class ScoringGenerationDTO {

    private String requestId;
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

    // Loan/Request features
    private String requestType;
    private String purpose;
    private Double loanAmount;
    private Integer termMonths;
    private Double interestRate;

    // Risk/Credit history features
    private Double ltv;
    private Double dti;
    private Integer previousLoansCount;
    private Integer previousDefaultsCount;

    public ScoringGenerationDTO() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
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
                "requestId='" + requestId + '\'' +
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
                ", requestType='" + requestType + '\'' +
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
