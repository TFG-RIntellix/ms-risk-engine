package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input;

/**
 * Data Transfer Object (DTO) for a top contributing feature in the SHAP
 * explainability.
 * Represents a single feature with its name, value at calculation time, and
 * SHAP contribution.
 *
 * @author Lucía Fernández Mancebo
 * @date 03/03/2026
 */
public class TopFeatureDTO {

    private String featureName;
    private String featureValue;
    private Double shapValue;

    public TopFeatureDTO() {
    }

    public TopFeatureDTO(String featureName, String featureValue, Double shapValue) {
        this.featureName = featureName;
        this.featureValue = featureValue;
        this.shapValue = shapValue;
    }

    // Getters and Setters

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureValue() {
        return featureValue;
    }

    public void setFeatureValue(String featureValue) {
        this.featureValue = featureValue;
    }

    public Double getShapValue() {
        return shapValue;
    }

    public void setShapValue(Double shapValue) {
        this.shapValue = shapValue;
    }

}
