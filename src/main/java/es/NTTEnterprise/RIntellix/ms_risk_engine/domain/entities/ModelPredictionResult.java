package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the prediction response returned by the AI risk model.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 03-05-2026
 */
public class ModelPredictionResult {
    // TODO: Abstract the json from the domain because we dont want dependencies.
    // Hexagonal architecture must be respected.
    @JsonProperty("probability_of_default")
    private Double probabilityOfDefault;

    @JsonProperty("risk_segment")
    private String riskSegment;

    @JsonProperty("base_value")
    private Double baseValue;

    @JsonProperty("shap_explanations")
    private List<RiskFeature> shapExplanations;

    /**
     * Constructor of the ModelPredictionResult class.
     */
    public ModelPredictionResult() {
    }

    /**
     * Constructor of the ModelPredictionResult class.
     *
     * @param probabilityOfDefault the probability of default.
     * @param riskSegment          the risk segment returned by the model.
     * @param baseValue            the SHAP base value.
     * @param shapExplanations     the SHAP top feature explanations.
     */
    public ModelPredictionResult(final Double probabilityOfDefault,
            final String riskSegment,
            final Double baseValue,
            final List<RiskFeature> shapExplanations) {
        this.probabilityOfDefault = probabilityOfDefault;
        this.riskSegment = riskSegment;
        this.baseValue = baseValue;
        this.shapExplanations = shapExplanations;
    }

    public Double getProbabilityOfDefault() {
        return probabilityOfDefault;
    }

    public void setProbabilityOfDefault(final Double probabilityOfDefault) {
        this.probabilityOfDefault = probabilityOfDefault;
    }

    public String getRiskSegment() {
        return riskSegment;
    }

    public void setRiskSegment(final String riskSegment) {
        this.riskSegment = riskSegment;
    }

    public Double getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(final Double baseValue) {
        this.baseValue = baseValue;
    }

    public List<RiskFeature> getShapExplanations() {
        return shapExplanations;
    }

    public void setShapExplanations(final List<RiskFeature> shapExplanations) {
        this.shapExplanations = shapExplanations;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(probabilityOfDefault);
        result = prime * result + Objects.hashCode(riskSegment);
        result = prime * result + Objects.hashCode(baseValue);
        result = prime * result + Objects.hashCode(shapExplanations);
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
        final ModelPredictionResult other = (ModelPredictionResult) obj;
        return Objects.equals(probabilityOfDefault, other.probabilityOfDefault)
                && Objects.equals(riskSegment, other.riskSegment)
                && Objects.equals(baseValue, other.baseValue)
                && Objects.equals(shapExplanations, other.shapExplanations);
    }

    @Override
    public String toString() {
        return "ModelPredictionResult{" +
                "probabilityOfDefault=" + probabilityOfDefault +
                ", riskSegment='" + riskSegment + '\'' +
                ", baseValue=" + baseValue +
                ", shapExplanations=" + shapExplanations +
                '}';
    }
}
