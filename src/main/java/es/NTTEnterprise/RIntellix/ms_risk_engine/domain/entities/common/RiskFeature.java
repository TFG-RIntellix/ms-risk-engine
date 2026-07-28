package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common;

import java.util.Objects;

/**
 * Represents one explainability feature returned by the scoring model.
 * Pure domain entity without any infrastructure library dependencies (Jackson,
 * etc.).
 * JSON serialization/deserialization is handled at the infrastructure layer.
 *
 * @author Lucía Fernández Mancebo
 * @date 25/04/2026
 */
public class RiskFeature {

    private String featureName;

    private String featureValue;

    private Double shapValue;

    private String description;

    /**
     * Constructor of the RiskFeature class.
     */
    public RiskFeature() {
    }

    /**
     * Constructor of the RiskFeature class.
     *
     * @param featureName  the feature name.
     * @param featureValue the feature value used by the model.
     * @param shapValue    the SHAP contribution value.
     * @param description  the human-readable direction/description.
     */
    public RiskFeature(final String featureName,
            final String featureValue,
            final Double shapValue,
            final String description) {
        this.featureName = featureName;
        this.featureValue = featureValue;
        this.shapValue = shapValue;
        this.description = description;
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(featureName);
        result = prime * result + Objects.hashCode(featureValue);
        result = prime * result + Objects.hashCode(shapValue);
        result = prime * result + Objects.hashCode(description);
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
        final RiskFeature other = (RiskFeature) obj;
        return Objects.equals(featureName, other.featureName)
                && Objects.equals(featureValue, other.featureValue)
                && Objects.equals(shapValue, other.shapValue)
                && Objects.equals(description, other.description);
    }

    @Override
    public String toString() {
        return "RiskFeature{" +
                "featureName='" + featureName + '\'' +
                ", featureValue='" + featureValue + '\'' +
                ", shapValue=" + shapValue +
                ", description='" + description + '\'' +
                '}';
    }
}
