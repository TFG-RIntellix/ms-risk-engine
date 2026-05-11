package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation;
import java.util.Date;
import java.util.List;

/**
 * Domain entity representing the credit risk scoring result calculated for a
 * request.
 * Includes model metadata, input features snapshot, risk metrics and SHAP
 * explainability.
 *
 * @author Lucía Fernández Mancebo
 *         Date: 03-02-2026
 */
public class Scoring {

    private String id;
    private String requestId;
    private String modelVersion;
    private Date executionDate;
    private ModelInputs inputSnapshot;
    private RiskMetrics results;
    private Double baseValue;
    private List<RiskFeature> explainability;

    /**
     * Default constructor for Scoring.
     */
    public Scoring() {
    }

    /**
     * Parameterized constructor for Scoring.
     *
     * @param id             Unique identifier.
     * @param requestId      Reference to the evaluated request.
     * @param modelVersion   Version of the model used (e.g. xgboost_pd_v1).
     * @param executionDate  Date and time when the scoring was computed.
     * @param inputSnapshot  Snapshot of the input features used by the model.
     * @param results        Risk metrics computed by the model (PD, LGD, EAD, ECL,
     *                       risk grade).
     * @param baseValue      SHAP base value (expected value) of the model.
     * @param explainability Top contributing features with their SHAP values.
     */
    public Scoring(String id, String requestId, String modelVersion, Date executionDate,
                   ModelInputs inputSnapshot, RiskMetrics results, Double baseValue,
                   List<RiskFeature> explainability) {
        this.id = id;
        this.requestId = requestId;
        this.modelVersion = modelVersion;
        this.executionDate = executionDate;
        this.inputSnapshot = inputSnapshot;
        this.results = results;
        this.baseValue = baseValue;
        this.explainability = explainability;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public ModelInputs getInputSnapshot() {
        return inputSnapshot;
    }

    public void setInputSnapshot(ModelInputs inputSnapshot) {
        this.inputSnapshot = inputSnapshot;
    }

    public RiskMetrics getResults() {
        return results;
    }

    public void setResults(RiskMetrics results) {
        this.results = results;
    }

    public Double getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(Double baseValue) {
        this.baseValue = baseValue;
    }

    public List<RiskFeature> getExplainability() {
        return explainability;
    }

    public void setExplainability(List<RiskFeature> explainability) {
        this.explainability = explainability;
    }

    // toString, hashCode and equals
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scoring{");
        sb.append("id=").append(id);
        sb.append(", requestId=").append(requestId);
        sb.append(", modelVersion=").append(modelVersion);
        sb.append(", executionDate=").append(executionDate);
        sb.append(", inputSnapshot=").append(inputSnapshot);
        sb.append(", results=").append(results);
        sb.append(", baseValue=").append(baseValue);
        sb.append(", explainability=").append(explainability);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
        result = prime * result + ((modelVersion == null) ? 0 : modelVersion.hashCode());
        result = prime * result + ((executionDate == null) ? 0 : executionDate.hashCode());
        result = prime * result + ((inputSnapshot == null) ? 0 : inputSnapshot.hashCode());
        result = prime * result + ((results == null) ? 0 : results.hashCode());
        result = prime * result + ((baseValue == null) ? 0 : baseValue.hashCode());
        result = prime * result + ((explainability == null) ? 0 : explainability.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Scoring other = (Scoring) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (requestId == null) {
            if (other.requestId != null)
                return false;
        } else if (!requestId.equals(other.requestId))
            return false;
        if (modelVersion == null) {
            if (other.modelVersion != null)
                return false;
        } else if (!modelVersion.equals(other.modelVersion))
            return false;
        if (executionDate == null) {
            if (other.executionDate != null)
                return false;
        } else if (!executionDate.equals(other.executionDate))
            return false;
        if (inputSnapshot == null) {
            if (other.inputSnapshot != null)
                return false;
        } else if (!inputSnapshot.equals(other.inputSnapshot))
            return false;
        if (results == null) {
            if (other.results != null)
                return false;
        } else if (!results.equals(other.results))
            return false;
        if (baseValue == null) {
            if (other.baseValue != null)
                return false;
        } else if (!baseValue.equals(other.baseValue))
            return false;
        if (explainability == null) {
            if (other.explainability != null)
                return false;
        } else if (!explainability.equals(other.explainability))
            return false;
        return true;
    }

}
