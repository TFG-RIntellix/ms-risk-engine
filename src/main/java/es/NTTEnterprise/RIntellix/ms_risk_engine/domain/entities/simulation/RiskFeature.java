package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation;

import lombok.Builder;

/**
 * Class representing the individual features that contribute to the risk
 * assessment in the scoring model.
 * Each RiskFeature encapsulates the name of the feature, its SHAP value (which
 * quantifies its contribution to the final risk score),
 * and a human-readable description for explainability purposes.
 * This class is used in the Scoring results to provide detailed insights into
 * which features are driving the risk assessment for a given contract.
 *
 * @author Lucía Fernández Mancebo
 *         Date: 03-02-2026
 */
@Builder
public class RiskFeature {

    private String featureName;
    private String featureValue;
    private Double shapValue;
    private String description;

    /**
     * Default constructor for RiskFeature.
     */
    public RiskFeature() {
    }

    /**
     * Parameterized constructor for RiskFeature. Allows setting all fields at once.
     *
     * @param featureName  The name of the feature (e.g., "Income", "Credit Score")
     *                     that contributed to the risk assessment.
     * @param featureValue The value of the feature at the time of calculation,
     *                     stored as String for uniformity.
     * @param shapValue    The SHAP value representing the contribution of this
     *                     feature to the final risk score. Positive values indicate
     *                     increased risk, while negative values indicate decreased
     *                     risk.
     * @param description  A human-readable description of the feature and its
     *                     impact on the risk score, used for explainability
     *                     purposes in the scoring results.
     */
    public RiskFeature(String featureName, String featureValue, Double shapValue, String description) {
        this.featureName = featureName;
        this.featureValue = featureValue;
        this.shapValue = shapValue;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // toString, hashCode and equals
    @Override
    public String toString() {
        return "RiskFeature [featureName=" + featureName + ", featureValue=" + featureValue + ", shapValue=" + shapValue
                + ", description=" + description + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((featureName == null) ? 0 : featureName.hashCode());
        result = prime * result + ((featureValue == null) ? 0 : featureValue.hashCode());
        result = prime * result + ((shapValue == null) ? 0 : shapValue.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RiskFeature other = (RiskFeature) obj;
        if (featureName == null) {
            if (other.featureName != null)
                return false;
        } else if (!featureName.equals(other.featureName))
            return false;
        if (featureValue == null) {
            if (other.featureValue != null)
                return false;
        } else if (!featureValue.equals(other.featureValue))
            return false;
        if (shapValue == null) {
            if (other.shapValue != null)
                return false;
        } else if (!shapValue.equals(other.shapValue))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        return true;
    }
}
