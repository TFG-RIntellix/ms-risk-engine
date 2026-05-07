package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a complete scoring execution result.
 *
 * Stores model metadata, model input snapshot, risk metrics,
 * and explainability top features.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 04-25-2026
 */
public class Scoring {

    private String id;
    private String requestId;
    private String modelVersion;
    private Date executionDate;
    private Map<String, Object> inputSnapshot;
    private RiskMetrics results;
    private Double baseValue;
    private List<RiskFeature> explainability;

    /**
     * Constructor of the Scoring class.
     */
    public Scoring() {
    }

    /**
     * Constructor of the Scoring class.
     *
     * @param id             the scoring identifier.
     * @param requestId      the request identifier associated to scoring.
     * @param modelVersion   the model version used for the prediction.
     * @param executionDate  the execution date of scoring.
     * @param inputSnapshot  the model input values snapshot.
     * @param results        the risk metrics results.
     * @param baseValue      the SHAP base value.
     * @param explainability the list of explainability top features.
     */
    public Scoring(final String id,
            final String requestId,
            final String modelVersion,
            final Date executionDate,
            final Map<String, Object> inputSnapshot,
            final RiskMetrics results,
            final Double baseValue,
            final List<RiskFeature> explainability) {
        this.id = id;
        this.requestId = requestId;
        this.modelVersion = modelVersion;
        this.executionDate = executionDate;
        this.inputSnapshot = inputSnapshot;
        this.results = results;
        this.baseValue = baseValue;
        this.explainability = explainability;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

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

    public RiskMetrics getResults() {
        return results;
    }

    public void setResults(final RiskMetrics results) {
        this.results = results;
    }

    public Double getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(final Double baseValue) {
        this.baseValue = baseValue;
    }

    public List<RiskFeature> getExplainability() {
        return explainability;
    }

    public void setExplainability(final List<RiskFeature> explainability) {
        this.explainability = explainability;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(id);
        result = prime * result + Objects.hashCode(requestId);
        result = prime * result + Objects.hashCode(modelVersion);
        result = prime * result + Objects.hashCode(executionDate);
        result = prime * result + Objects.hashCode(inputSnapshot);
        result = prime * result + Objects.hashCode(results);
        result = prime * result + Objects.hashCode(baseValue);
        result = prime * result + Objects.hashCode(explainability);
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
        final Scoring other = (Scoring) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(requestId, other.requestId)
                && Objects.equals(modelVersion, other.modelVersion)
                && Objects.equals(executionDate, other.executionDate)
                && Objects.equals(inputSnapshot, other.inputSnapshot)
                && Objects.equals(results, other.results)
                && Objects.equals(baseValue, other.baseValue)
                && Objects.equals(explainability, other.explainability);
    }

    @Override
    public String toString() {
        return "Scoring{" +
                "id='" + id + '\'' +
                ", requestId='" + requestId + '\'' +
                ", modelVersion='" + modelVersion + '\'' +
                ", executionDate=" + executionDate +
                ", inputSnapshot=" + inputSnapshot +
                ", results=" + results +
                ", baseValue=" + baseValue +
                ", explainability=" + explainability +
                '}';
    }
}
