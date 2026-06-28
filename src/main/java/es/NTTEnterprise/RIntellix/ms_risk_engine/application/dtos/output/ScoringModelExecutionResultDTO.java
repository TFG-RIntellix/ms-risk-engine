package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output;

import java.util.Map;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;

/**
 * Encapsulates the complete output of a model execution, including the
 * original request payload, the model's prediction, and the resulting risk
 * metrics.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-25-2026
 */
public class ScoringModelExecutionResultDTO {

    private Map<String, Object> modelRequestPayload;
    private ModelPredictionResult predictionResult;
    private RiskMetrics riskMetrics;
    private boolean isHardCutoff;

    /**
     * Constructor of the ScoringModelExecutionResultDTO class.
     */
    public ScoringModelExecutionResultDTO() {
    }

    /**
     * Constructor of the ScoringModelExecutionResultDTO class.
     *
     * @param modelRequestPayload the prepared payload sent to the model endpoint.
     * @param predictionResult    the prediction response returned by the model.
     * @param riskMetrics         the fully computed risk metrics.
     * @param isHardCutoff        whether this result bypassed the AI model due to hard-cutoff rule.
     */
    public ScoringModelExecutionResultDTO(
            final Map<String, Object> modelRequestPayload,
            final ModelPredictionResult predictionResult,
            final RiskMetrics riskMetrics,
            final boolean isHardCutoff) {
        this.modelRequestPayload = modelRequestPayload;
        this.predictionResult = predictionResult;
        this.riskMetrics = riskMetrics;
        this.isHardCutoff = isHardCutoff;
    }

    // Getters and setters.

    public Map<String, Object> getModelRequestPayload() {
        return modelRequestPayload;
    }

    public void setModelRequestPayload(final Map<String, Object> modelRequestPayload) {
        this.modelRequestPayload = modelRequestPayload;
    }

    public ModelPredictionResult getPredictionResult() {
        return predictionResult;
    }

    public void setPredictionResult(final ModelPredictionResult predictionResult) {
        this.predictionResult = predictionResult;
    }

    public RiskMetrics getRiskMetrics() {
        return riskMetrics;
    }

    public void setRiskMetrics(final RiskMetrics riskMetrics) {
        this.riskMetrics = riskMetrics;
    }

    public boolean isHardCutoff() {
        return isHardCutoff;
    }

    public void setHardCutoff(boolean isHardCutoff) {
        this.isHardCutoff = isHardCutoff;
    }

    @Override
    public String toString() {
        return "ScoringModelExecutionResultDTO [modelRequestPayload=" + modelRequestPayload
                + ", predictionResult=" + predictionResult + ", riskMetrics=" + riskMetrics + ", isHardCutoff="
                + isHardCutoff + "]";
    }

}
