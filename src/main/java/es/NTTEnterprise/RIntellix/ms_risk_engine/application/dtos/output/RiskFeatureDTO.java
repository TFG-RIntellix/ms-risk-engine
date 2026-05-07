package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output;

/**
 * Output DTO representing a single explainability feature
 * for Kafka scoring result message transport.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-26-2026
 */
public class RiskFeatureDTO {

    private String featureName;
    private String featureValue;
    private Double shapValue;
    private String description;

    /**
     * Constructor of the RiskFeatureDTO class.
     */
    public RiskFeatureDTO() {
    }

    /**
     * Constructor of the RiskFeatureDTO class.
     *
     * @param featureName  the feature name.
     * @param featureValue the feature value used by the model.
     * @param shapValue    the SHAP contribution value.
     * @param description  the human-readable direction or description.
     */
    public RiskFeatureDTO(final String featureName,
            final String featureValue,
            final Double shapValue,
            final String description) {
        this.featureName = featureName;
        this.featureValue = featureValue;
        this.shapValue = shapValue;
        this.description = description;
    }

    // Getters and setters.

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(final String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureValue() {
        return featureValue;
    }

    public void setFeatureValue(final String featureValue) {
        this.featureValue = featureValue;
    }

    public Double getShapValue() {
        return shapValue;
    }

    public void setShapValue(final Double shapValue) {
        this.shapValue = shapValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
