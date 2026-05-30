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
     */
    public ScoringModelExecutionResultDTO(
            final Map<String, Object> modelRequestPayload,
            final ModelPredictionResult predictionResult,
            final RiskMetrics riskMetrics) {
        this.modelRequestPayload = modelRequestPayload;
        this.predictionResult = predictionResult;
        this.riskMetrics = riskMetrics;
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

    @Override
    public String toString() {
        return "ScoringModelExecutionResultDTO [modelRequestPayload=" + modelRequestPayload
                + ", predictionResult=" + predictionResult + ", riskMetrics=" + riskMetrics + "]";
    }

}
