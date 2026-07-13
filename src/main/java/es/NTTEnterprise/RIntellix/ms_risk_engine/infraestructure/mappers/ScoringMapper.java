package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.mappers;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.TopFeatureDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.FinancialMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.ModelInputs;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskFeature;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.NamingConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapper class responsible for converting ScoringDTO objects into Scoring
 * entities.
 * This class serves as a bridge between the data transfer objects (DTOs)
 * used for communication with external services and the domain entities
 * used within the application.
 * It encapsulates the logic for mapping the fields from the ScoringDTO to the
 * Scoring entity,
 * including the risk metrics, explainability features, and input snapshot.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
@Component
public class ScoringMapper {

    private final NamingConverter namingConverter;

    public ScoringMapper(final NamingConverter namingConverter) {
        this.namingConverter = Objects.requireNonNull(namingConverter,
                LogMessage.NAMING_CONVERTER_CANNOT_BE_NULL);
    }

    /**
     * Maps a ScoringDTO object to a Scoring entity.
     * This method takes a ScoringDTO, which is a data transfer object typically
     * used for
     * communication with external services, and converts it into a Scoring entity
     * that is used
     * within the domain layer of the application. The mapping includes transferring
     * basic fields,
     * as well as converting the risk metrics, explainability features, and input
     * snapshot into
     * their corresponding domain representations.
     * 
     * @param scoringDTO The ScoringDTO object to be mapped, containing the data
     *                   retrieved from an external source.
     * @return A Scoring entity populated with the data from the ScoringDTO.
     *         If the input ScoringDTO is null, null is returned.
     */
    public Scoring toDomain(ScoringDTO scoringDTO) {

        if (scoringDTO == null) {
            return null;
        }

        final Scoring scoring = new Scoring();
        scoring.setId(scoringDTO.getScoringId());
        scoring.setRequestId(scoringDTO.getRequestId());
        scoring.setBaseValue(scoringDTO.getBaseValue());
        if (scoringDTO.getScoringDate() != null) {
            scoring.setExecutionDate(Date.from(LocalDateTime.parse(scoringDTO.getScoringDate())
                    .atZone(ZoneId.systemDefault()).toInstant()));
        }

        // NOTE: Scoring input snapshot uses model field names (mapped via
        // LoanOrMortgageModelPayloadMapper during scoring generation).
        // For simulation draft merges, SimulationModelPayloadMapper handles
        // transformation of form changes from API to model field names.
        scoring.setModelVersion(scoringDTO.getModelVersion());
        scoring.setResults(mapRiskMetrics(scoringDTO));
        scoring.setExplainability(mapExplainability(scoringDTO.getTopFeatures()));
        scoring.setInputSnapshot(mapInputSnapshot(scoringDTO).getFeatures());

        return scoring;

    }

    /**
     * Maps the risk metrics from the ScoringDTO to a RiskMetrics entity.
     * This method extracts the relevant risk metrics (PD, LGD, EAD, ECL) from the
     * ScoringDTO.
     * 
     * @param scoringDTO The ScoringDTO containing the risk metrics to be mapped.
     * @return A RiskMetrics entity populated with the values from the ScoringDTO.
     */
    private RiskMetrics mapRiskMetrics(ScoringDTO scoringDTO) {
        RiskMetrics riskMetrics = new RiskMetrics();
        riskMetrics.setProbabilityOfDefault(scoringDTO.getPd());
        riskMetrics.setLossGivenDefault(scoringDTO.getLgd());
        riskMetrics.setExposureAtDefault(scoringDTO.getEad());
        riskMetrics.setExpectedCalculatedLoss(scoringDTO.getEcl());
        riskMetrics.setRiskLevel(scoringDTO.getRiskGrade());

        // Map financial metrics if available
        if (scoringDTO.getMonthlyPayment() != null) {
            FinancialMetrics financialMetrics = new FinancialMetrics();
            financialMetrics.setMonthlyPayment(scoringDTO.getMonthlyPayment());
            financialMetrics.setDebtToIncomeRatio(scoringDTO.getDti());
            financialMetrics.setTotalPayment(scoringDTO.getTotalPayment());
            financialMetrics.setTotalInterest(scoringDTO.getTotalInterest());
            financialMetrics.setMonthlyDisposableIncome(scoringDTO.getMonthlyDisposableIncome());
            riskMetrics.setFinancialMetrics(financialMetrics);
        }

        return riskMetrics;
    }

    /**
     * Maps the explainability features from the list of TopFeatureDTO to a list of
     * RiskFeature entities.
     * This method converts each TopFeatureDTO, which contains the feature name,
     * value and SHAP value,
     * into a RiskFeature entity that can be used in the domain layer.
     * 
     * @param featureImportanceDTOs The list of TopFeatureDTO objects representing
     *                              the
     *                              top contributing features and their SHAP values.
     * @return A list of RiskFeature entities mapped from the provided
     *         TopFeatureDTOs. If the input list is null, an empty list is returned.
     *
     */
    private List<RiskFeature> mapExplainability(List<TopFeatureDTO> featureImportanceDTOs) {
        if (featureImportanceDTOs == null) {
            return Collections.emptyList();
        }

        return featureImportanceDTOs.stream()
                .map(dto -> {
                    RiskFeature feature = new RiskFeature();
                    feature.setFeatureName(dto.getFeatureName());
                    feature.setFeatureValue(dto.getFeatureValue());
                    feature.setShapValue(dto.getShapValue());
                    return feature;
                })
                .collect(Collectors.toList());
    }

    /**
     * Maps the input features from the ScoringDTO to a ModelInputs entity.
     * This method takes the input features provided in the ScoringDTO and converts
     * them into a
     * ModelInputs entity that encapsulates the features in a format suitable for
     * the domain layer.
     * Keys are normalized from snake_case to camelCase and non-canonical aliases
     * are resolved to their canonical model field names.
     * 
     * @param scoringDTO The ScoringDTO containing the input features to be mapped.
     * @return A ModelInputs entity populated with the features from the ScoringDTO.
     *         If the input features are null, an empty ModelInputs object is
     *         returned.
     */
    private ModelInputs mapInputSnapshot(ScoringDTO scoringDTO) {
        if (scoringDTO.getInputFeatures() == null) {
            return new ModelInputs();
        }

        final Map<String, Object> source = scoringDTO.getInputFeatures();
        final HashMap<String, Object> normalized = new HashMap<>();

        for (final Map.Entry<String, Object> entry : source.entrySet()) {
            final String canonicalKey = resolveCanonicalFieldName(entry.getKey());
            normalized.put(canonicalKey, entry.getValue());
        }

        final ModelInputs modelInputs = new ModelInputs();
        modelInputs.setFeatures(normalized);
        return modelInputs;
    }

    /**
     * Resolves a raw field name to its canonical model payload field name.
     * First converts from snake_case to camelCase, then applies alias translations.
     *
     * @param rawFieldName The raw field name from the database snapshot.
     * @return The canonical field name used in the domain layer.
     */
    private String resolveCanonicalFieldName(final String rawFieldName) {
        final String camelCaseFieldName = namingConverter.toCamelCase(rawFieldName);
        return ModelPayloadFieldNames.FIELD_ALIASES.getOrDefault(
                camelCaseFieldName, camelCaseFieldName);
    }

}
