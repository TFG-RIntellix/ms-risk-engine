package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.RiskFeature;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Scoring;

/**
 * Mapper that converts model execution output into Scoring domain entity.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 04-25-2026
 */
@Component
public class ScoringResultMapper {

    /**
     * Maps prepared model payload and prediction output into Scoring entity.
     *
     * @param requestId        the request identifier.
     * @param modelVersion     the model version used in execution.
     * @param inputSnapshot    the payload sent to the model.
     * @param predictionResult the prediction response from model.
     * @param riskMetrics      the fully computed risk metrics (PD, EAD, LGD, ECL,
     *                         RiskGrade).
     * @return the mapped scoring domain object.
     */
    public Scoring toScoring(
            final String requestId,
            final String modelVersion,
            final Map<String, Object> inputSnapshot,
            final ModelPredictionResult predictionResult,
            final RiskMetrics riskMetrics) {

        final Scoring scoring = new Scoring();
        scoring.setRequestId(requestId);
        scoring.setModelVersion(modelVersion);
        scoring.setExecutionDate(new Date());
        scoring.setInputSnapshot(inputSnapshot);
        scoring.setResults(riskMetrics);
        scoring.setBaseValue(predictionResult.getBaseValue());
        scoring.setExplainability(copyExplainability(predictionResult.getShapExplanations(), inputSnapshot));
        return scoring;
    }

    private List<RiskFeature> copyExplainability(
            final List<RiskFeature> shapExplanations,
            final Map<String, Object> inputSnapshot) {

        if (shapExplanations == null || shapExplanations.isEmpty()) {
            return List.of();
        }

        final List<RiskFeature> explainability = new ArrayList<>();
        for (RiskFeature sourceFeature : shapExplanations) {
            final RiskFeature mappedFeature = new RiskFeature();
            mappedFeature.setFeatureName(sourceFeature.getFeatureName());
            mappedFeature.setShapValue(sourceFeature.getShapValue());
            mappedFeature.setDescription(sourceFeature.getDescription());
            mappedFeature.setFeatureValue(resolveFeatureValue(sourceFeature, inputSnapshot));
            explainability.add(mappedFeature);
        }
        return explainability;
    }

    private String resolveFeatureValue(final RiskFeature sourceFeature, final Map<String, Object> inputSnapshot) {
        final String featureName = sourceFeature.getFeatureName();
        if (featureName == null || featureName.isBlank()) {
            return "";
        }

        final Object rawValue = findValueIgnoreCase(inputSnapshot, featureName);
        return rawValue == null ? "" : String.valueOf(rawValue);
    }

    private Object findValueIgnoreCase(final Map<String, Object> map, final String key) {
        final Object direct = map.get(key);
        if (direct != null) {
            return direct;
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
