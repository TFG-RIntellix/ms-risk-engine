package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.RiskFeatureDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.RiskMetricsDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.ScoringResultMessageDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.RiskFeature;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Scoring;

/**
 * Mapper that converts a Scoring domain entity into a
 * ScoringResultMessageDTO for Kafka message publishing.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-26-2026
 */
@Component
public class ScoringResultMessageDTOMapper {

    /**
     * Maps a Scoring domain entity to a ScoringResultMessageDTO.
     *
     * @param scoring the scoring domain entity.
     * @return the mapped output DTO, or null if input is null.
     */
    public ScoringResultMessageDTO toDTO(final Scoring scoring) {
        if (scoring == null) {
            return null;
        }

        System.out.println("Mapping Scoring entity to ScoringResultMessageDTO");
        System.out.println("This is the actual scoring: " + scoring.toString());

        final ScoringResultMessageDTO dto = new ScoringResultMessageDTO();
        dto.setRequestId(scoring.getRequestId());
        dto.setModelVersion(scoring.getModelVersion());
        dto.setExecutionDate(scoring.getExecutionDate());
        dto.setInputSnapshot(scoring.getInputSnapshot());
        dto.setResults(mapRiskMetrics(scoring.getResults()));
        dto.setBaseValue(scoring.getBaseValue());
        dto.setExplainability(mapExplainability(scoring.getExplainability()));
        return dto;
    }

    private RiskMetricsDTO mapRiskMetrics(final RiskMetrics riskMetrics) {
        if (riskMetrics == null) {
            return null;
        }

        final RiskMetricsDTO dto = new RiskMetricsDTO();
        dto.setProbabilityOfDefault(riskMetrics.getProbabilityOfDefault());
        dto.setLossGivenDefault(riskMetrics.getLossGivenDefault());
        dto.setExposureAtDefault(riskMetrics.getExposureAtDefault());
        dto.setExpectedCalculatedLoss(riskMetrics.getExpectedCalculatedLoss());
        dto.setRiskLevel(riskMetrics.getRiskLevel());
        return dto;
    }

    private List<RiskFeatureDTO> mapExplainability(final List<RiskFeature> features) {
        if (features == null || features.isEmpty()) {
            return List.of();
        }

        return features.stream()
                .map(this::mapRiskFeature)
                .toList();
    }

    private RiskFeatureDTO mapRiskFeature(final RiskFeature feature) {
        if (feature == null) {
            return null;
        }

        final RiskFeatureDTO dto = new RiskFeatureDTO();
        dto.setFeatureName(feature.getFeatureName());
        dto.setFeatureValue(feature.getFeatureValue());
        dto.setShapValue(feature.getShapValue());
        dto.setDescription(feature.getDescription());
        return dto;
    }
}
