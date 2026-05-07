package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Output DTO representing the complete scoring result message
 * published to Kafka for persistence by ms-core-data.
 *
 * Contains all scoring metadata, model input snapshot, risk metrics,
 * and explainability top features.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-26-2026
 */
public class ScoringResultMessageDTO {

    private String requestId;
    private String modelVersion;
    private Date executionDate;
    private Map<String, Object> inputSnapshot;
    private RiskMetricsDTO results;
    private Double baseValue;
    private List<RiskFeatureDTO> explainability;

    /**
     * Constructor of the ScoringResultMessageDTO class.
     */
    public ScoringResultMessageDTO() {
    }

    /**
     * Constructor of the ScoringResultMessageDTO class.
     *
     * @param requestId      the request identifier associated to scoring.
     * @param modelVersion   the model version used for the prediction.
     * @param executionDate  the execution date of scoring.
     * @param inputSnapshot  the model input values snapshot.
     * @param results        the risk metrics results.
     * @param baseValue      the SHAP base value.
     * @param explainability the list of explainability top features.
     */
    public ScoringResultMessageDTO(final String requestId,
            final String modelVersion,
            final Date executionDate,
            final Map<String, Object> inputSnapshot,
            final RiskMetricsDTO results,
            final Double baseValue,
            final List<RiskFeatureDTO> explainability) {
        this.requestId = requestId;
        this.modelVersion = modelVersion;
        this.executionDate = executionDate;
        this.inputSnapshot = inputSnapshot;
        this.results = results;
        this.baseValue = baseValue;
        this.explainability = explainability;
    }

    // Getters and setters.

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(final String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(final Date executionDate) {
        this.executionDate = executionDate;
    }

    public Map<String, Object> getInputSnapshot() {
        return inputSnapshot;
    }

    public void setInputSnapshot(final Map<String, Object> inputSnapshot) {
        this.inputSnapshot = inputSnapshot;
    }

    public RiskMetricsDTO getResults() {
        return results;
    }

    public void setResults(final RiskMetricsDTO results) {
        this.results = results;
    }

    public Double getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(final Double baseValue) {
        this.baseValue = baseValue;
    }

    public List<RiskFeatureDTO> getExplainability() {
        return explainability;
    }

    public void setExplainability(final List<RiskFeatureDTO> explainability) {
        this.explainability = explainability;
    }

    @Override
    public String toString() {
        return "ScoringResultMessageDTO [requestId=" + requestId + ", modelVersion=" + modelVersion
                + ", executionDate=" + executionDate + ", inputSnapshot=" + inputSnapshot + ", results=" + results
                + ", baseValue=" + baseValue + ", explainability=" + explainability + "]";
    }
}
