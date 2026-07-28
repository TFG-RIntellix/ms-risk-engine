package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.output.dtos;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskFeature;

/**
 * Data Transfer Object for a risk feature returned by the AI model.
 * This DTO handles JSON serialization/deserialization at the infrastructure
 * boundary.
 * It maps model API field names (snake_case) to domain entity properties
 * (camelCase).
 *
 * Responsibilities:
 * - Deserialize model API JSON responses into typed objects
 * - Map external JSON field names to internal property names
 * - Provide conversion to domain entity (RiskFeature)
 *
 * @author Lucia Fernandez Mancebo
 * @Date 09-05-2026
 */
public class RiskFeatureResponseDTO {

    @JsonProperty("feature")
    private String featureName;

    @JsonProperty("feature_value")
    private String featureValue;

    @JsonProperty("impact")
    private Double shapValue;

    @JsonProperty("direction")
    private String description;

    /**
     * Constructor of the RiskFeatureResponseDTO class.
     */
    public RiskFeatureResponseDTO() {
    }

    /**
     * Constructor of the RiskFeatureResponseDTO class.
     *
     * @param featureName  the feature name.
     * @param featureValue the feature value used by the model.
     * @param shapValue    the SHAP contribution value.
     * @param description  the human-readable direction/description.
     */
    public RiskFeatureResponseDTO(final String featureName,
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

    /**
     * Converts this DTO to a domain entity (RiskFeature).
     * This method is called after deserialization to move the data into the domain
     * layer.
     *
     * @return a RiskFeature domain entity with values mapped from this response
     */
    public RiskFeature toDomainEntity() {
        return new RiskFeature(
                this.featureName,
                this.featureValue,
                this.shapValue,
                this.description);
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
        final RiskFeatureResponseDTO other = (RiskFeatureResponseDTO) obj;
        return Objects.equals(featureName, other.featureName)
                && Objects.equals(featureValue, other.featureValue)
                && Objects.equals(shapValue, other.shapValue)
                && Objects.equals(description, other.description);
    }

    @Override
    public String toString() {
        return "RiskFeatureResponseDTO{" +
                "featureName='" + featureName + '\'' +
                ", featureValue='" + featureValue + '\'' +
                ", shapValue=" + shapValue +
                ", description='" + description + '\'' +
                '}';
    }
}
