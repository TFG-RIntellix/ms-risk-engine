package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.output.dtos;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskFeature;

/**
 * Data Transfer Object for the model prediction response from the AI risk
 * model.
 * This DTO handles JSON serialization/deserialization at the infrastructure
 * boundary.
 * It maps model API field names (snake_case) to domain entity properties
 * (camelCase).
 *
 * Responsibilities:
 * - Deserialize model API JSON responses into typed objects
 * - Map external JSON field names to internal property names
 * - Convert external RiskFeatureResponseDTO objects to domain RiskFeature
 * entities
 * - Provide conversion to domain entity (ModelPredictionResult)
 *
 * @author Lucía Fernández Mancebo
 * @date 05/09/2026
 */
public class ModelPredictionResponseDTO {

    @JsonProperty("probability_of_default")
    private Double probabilityOfDefault;

    @JsonProperty("risk_segment")
    private String riskSegment;

    @JsonProperty("base_value")
    private Double baseValue;

    @JsonProperty("shap_explanations")
    private List<RiskFeatureResponseDTO> shapExplanations;

    /**
     * Constructor of the ModelPredictionResponseDTO class.
     */
    public ModelPredictionResponseDTO() {
    }

    /**
     * Constructor of the ModelPredictionResponseDTO class.
     *
     * @param probabilityOfDefault the probability of default.
     * @param riskSegment          the risk segment returned by the model.
     * @param baseValue            the SHAP base value.
     * @param shapExplanations     the SHAP top feature explanations (as responses).
     */
    public ModelPredictionResponseDTO(final Double probabilityOfDefault,
            final String riskSegment,
            final Double baseValue,
            final List<RiskFeatureResponseDTO> shapExplanations) {
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

    public List<RiskFeatureResponseDTO> getShapExplanations() {
        return shapExplanations;
    }

    public void setShapExplanations(final List<RiskFeatureResponseDTO> shapExplanations) {
        this.shapExplanations = shapExplanations;
    }

    /**
     * Converts this DTO to a domain entity (ModelPredictionResult).
     * This method:
     * 1. Converts all nested RiskFeatureResponseDTO objects to RiskFeature domain
     * entities
     * 2. Creates a ModelPredictionResult with the converted data
     * 3. This ensures the domain layer only receives pure domain objects
     *
     * @return a ModelPredictionResult domain entity with values mapped from this
     *         response
     */
    public ModelPredictionResult toDomainEntity() {
        final List<RiskFeature> domainRiskFeatures = this.shapExplanations == null
                ? null
                : this.shapExplanations.stream()
                        .map(RiskFeatureResponseDTO::toDomainEntity)
                        .collect(Collectors.toList());

        return new ModelPredictionResult(
                this.probabilityOfDefault,
                this.riskSegment,
                this.baseValue,
                domainRiskFeatures);
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
        final ModelPredictionResponseDTO other = (ModelPredictionResponseDTO) obj;
        return Objects.equals(probabilityOfDefault, other.probabilityOfDefault)
                && Objects.equals(riskSegment, other.riskSegment)
                && Objects.equals(baseValue, other.baseValue)
                && Objects.equals(shapExplanations, other.shapExplanations);
    }

    @Override
    public String toString() {
        return "ModelPredictionResponseDTO{" +
                "probabilityOfDefault=" + probabilityOfDefault +
                ", riskSegment='" + riskSegment + '\'' +
                ", baseValue=" + baseValue +
                ", shapExplanations=" + shapExplanations +
                '}';
    }
}
