package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.FinancialMetricsDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.RiskFeatureDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.RiskMetricsDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.ScoringResultMessageDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.FinancialMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskFeature;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapper that converts a Scoring domain entity into a
 * ScoringResultMessageDTO for Kafka message publishing.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-26-2026
 */
@Slf4j
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

        log.info(LogMessage.MAPPING_SCORING_ENTITY_TO_DTO);
        log.debug(LogMessage.ACTUAL_SCORING_DEBUG, scoring.toString());

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

    /**
     * Maps a RiskMetrics domain entity to a RiskMetricsDTO.
     * Includes mapping of financial metrics if present.
     *
     * @param riskMetrics the riskMetrics domain entity.
     * @return the mapped output DTO, or null if input is null.
     */
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

        // Map financial metrics if present
        if (riskMetrics.getFinancialMetrics() != null) {
            dto.setFinancialMetrics(mapFinancialMetrics(riskMetrics.getFinancialMetrics()));
        }

        return dto;
    }

    /**
     * Maps a FinancialMetrics domain entity to a FinancialMetricsDTO.
     *
     * @param financialMetrics the financialMetrics domain entity.
     * @return the mapped output DTO, or null if input is null.
     */
    private FinancialMetricsDTO mapFinancialMetrics(FinancialMetrics financialMetrics) {
        if (financialMetrics == null) {
            return null;
        }

        final FinancialMetricsDTO dto = new FinancialMetricsDTO();
        dto.setMonthlyPayment(financialMetrics.getMonthlyPayment());
        dto.setDebtToIncomeRatio(financialMetrics.getDebtToIncomeRatio());
        dto.setTotalPayment(financialMetrics.getTotalPayment());
        dto.setTotalInterest(financialMetrics.getTotalInterest());
        dto.setMonthlyDisposableIncome(financialMetrics.getMonthlyDisposableIncome());
        return dto;
    }

    /**
     * Maps a List of RiskFeature domain entities to a List of RiskFeatureDTO.
     *
     * @param features the List of RiskFeature domain entities.
     * @return the mapped List of RiskFeatureDTO, or an empty list if the input list
     *         is null or empty.
     */
    private List<RiskFeatureDTO> mapExplainability(final List<RiskFeature> features) {
        if (features == null || features.isEmpty()) {
            return List.of();
        }

        return features.stream()
                .map(this::mapRiskFeature)
                .toList();
    }

    /**
     * Maps a RiskFeature domain entity to a RiskFeatureDTO.
     * 
     * @param feature the RiskFeature domain entity.
     * @return the mapped output DTO, or null if input is null.
     */
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
