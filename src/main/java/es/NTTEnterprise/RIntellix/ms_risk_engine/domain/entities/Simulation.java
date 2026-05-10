package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities;

import java.util.Date;
import java.util.Objects;

/**
 * Represents a simulation scenario linked to a base scoring.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class Simulation {

    private String id;
    private String requestId;
    private String baseScoringsId;
    private String scenarioName;
    private Date createdAt;
    private FormChanges formChanges;
    private SimulationMetrics simulatedResults;
    private SimulationDelta delta;

    /**
     * Constructor of the Simulation class.
     */
    public Simulation() {
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

    public String getBaseScoringsId() {
        return baseScoringsId;
    }

    public void setBaseScoringsId(final String baseScoringsId) {
        this.baseScoringsId = baseScoringsId;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(final String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    public FormChanges getFormChanges() {
        return formChanges;
    }

    public void setFormChanges(final FormChanges formChanges) {
        this.formChanges = formChanges;
    }

    public SimulationMetrics getSimulatedResults() {
        return simulatedResults;
    }

    public void setSimulatedResults(final SimulationMetrics simulatedResults) {
        this.simulatedResults = simulatedResults;
    }

    public SimulationDelta getDelta() {
        return delta;
    }

    public void setDelta(final SimulationDelta delta) {
        this.delta = delta;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(id);
        result = prime * result + Objects.hashCode(requestId);
        result = prime * result + Objects.hashCode(baseScoringsId);
        result = prime * result + Objects.hashCode(scenarioName);
        result = prime * result + Objects.hashCode(createdAt);
        result = prime * result + Objects.hashCode(formChanges);
        result = prime * result + Objects.hashCode(simulatedResults);
        result = prime * result + Objects.hashCode(delta);
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
        final Simulation other = (Simulation) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(requestId, other.requestId)
                && Objects.equals(baseScoringsId, other.baseScoringsId)
                && Objects.equals(scenarioName, other.scenarioName)
                && Objects.equals(createdAt, other.createdAt)
                && Objects.equals(formChanges, other.formChanges)
                && Objects.equals(simulatedResults, other.simulatedResults)
                && Objects.equals(delta, other.delta);
    }

    @Override
    public String toString() {
        return "Simulation{" +
                "id='" + id + '\'' +
                ", requestId='" + requestId + '\'' +
                ", baseScoringsId='" + baseScoringsId + '\'' +
                ", scenarioName='" + scenarioName + '\'' +
                ", createdAt=" + createdAt +
                ", formChanges=" + formChanges +
                ", simulatedResults=" + simulatedResults +
                ", delta=" + delta +
                '}';
    }
}
