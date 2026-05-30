package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for the scoring result associated with a request.
 * Contains all information displayed in the scoring detail view:
 * model metadata, input features, risk metrics and SHAP explainability.
 *
 * @author Lucía Fernández Mancebo
 * @Date 03-03-2026
 */
public class ScoringDTO {

    private String scoringId;
    private String requestId;
    private String modelVersion;
    private String scoringDate;
    private Map<String, Object> inputFeatures;
    private Double pd;
    private Double lgd;
    private Double ead;
    private Double ecl;
    private String riskGrade;
    private Double monthlyPayment;
    private Double dti;
    private Double totalPayment;
    private Double totalInterest;
    private Double monthlyDisposableIncome;
    private Double baseValue;
    private List<TopFeatureDTO> topFeatures;

    public ScoringDTO() {
    }

    // Getters and Setters

    public String getScoringId() {
        return scoringId;
    }

    public void setScoringId(String scoringId) {
        this.scoringId = scoringId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getScoringDate() {
        return scoringDate;
    }

    public void setScoringDate(String scoringDate) {
        this.scoringDate = scoringDate;
    }

    public Map<String, Object> getInputFeatures() {
        return inputFeatures;
    }

    public void setInputFeatures(Map<String, Object> inputFeatures) {
        this.inputFeatures = inputFeatures;
    }

    public Double getPd() {
        return pd;
    }

    public void setPd(Double pd) {
        this.pd = pd;
    }

    public Double getLgd() {
        return lgd;
    }

    public void setLgd(Double lgd) {
        this.lgd = lgd;
    }

    public Double getEad() {
        return ead;
    }

    public void setEad(Double ead) {
        this.ead = ead;
    }

    public Double getEcl() {
        return ecl;
    }

    public void setEcl(Double ecl) {
        this.ecl = ecl;
    }

    public String getRiskGrade() {
        return riskGrade;
    }

    public void setRiskGrade(String riskGrade) {
        this.riskGrade = riskGrade;
    }

    public Double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(Double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public Double getDti() {
        return dti;
    }

    public void setDti(Double dti) {
        this.dti = dti;
    }

    public Double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public Double getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(Double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public Double getMonthlyDisposableIncome() {
        return monthlyDisposableIncome;
    }

    public void setMonthlyDisposableIncome(Double monthlyDisposableIncome) {
        this.monthlyDisposableIncome = monthlyDisposableIncome;
    }

    public Double getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(Double baseValue) {
        this.baseValue = baseValue;
    }

    public List<TopFeatureDTO> getTopFeatures() {
        return topFeatures;
    }

    public void setTopFeatures(List<TopFeatureDTO> topFeatures) {
        this.topFeatures = topFeatures;
    }

}
