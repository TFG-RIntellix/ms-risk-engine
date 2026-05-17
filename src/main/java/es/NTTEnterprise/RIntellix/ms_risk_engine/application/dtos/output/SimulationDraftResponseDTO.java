package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output;

import java.util.HashMap;

/**
 * DTO for representing the response of a simulation draft,
 * containing the simulated results, deltas, and other relevant information.
 *
 * 
 */
public class SimulationDraftResponseDTO {

    private String requestId; // Reference to the associated scoring request
    private String partyId; // Reference to the associated party (customer)
    private String baseScoringId;

    private HashMap<String, Object> formChanges;
    private SimulationMetricsResponseDTO simulatedResults;
    private SimulationDeltaResponseDTO delta;

    /**
     * Default constructor for SimulationDraftResponseDTO.
     * @return A new instance of SimulationDraftResponseDTO with default values.
     */
    public SimulationDraftResponseDTO () {
    }

    /**
     * Constructor for SimulationDraftResponseDTO.
     * @param requestId Reference to the associated scoring request
     * @param partyId Reference to the associated party (customer)
     * @param baseScoringId Reference to the base scoring used for the simulation
     * @param formChanges The changes made to the input features for the simulation
     * @param simulatedResults The resulting risk metrics from the simulation
     * @param delta The computed deltas (PD, EL, risk grade) compared to the original scoring
     */
    public SimulationDraftResponseDTO(String requestId, String partyId, String baseScoringId,
                                      HashMap<String, Object> formChanges, SimulationMetricsResponseDTO simulatedResults,
                                      SimulationDeltaResponseDTO delta, String simulatedDecision) {
        this.requestId = requestId;
        this.partyId = partyId;
        this.baseScoringId = baseScoringId;
        this.formChanges = formChanges;
        this.simulatedResults = simulatedResults;
        this.delta = delta;
    }

    // Getters and Setters

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(final String partyId) {
        this.partyId = partyId;
    }

    public String getBaseScoringId() {
        return baseScoringId;
    }

    public void setBaseScoringId(final String baseScoringId) {
        this.baseScoringId = baseScoringId;
    }

    public HashMap<String, Object> getFormChanges() {
        return formChanges;
    }

    public void setFormChanges(final HashMap<String, Object> formChanges) {
        this.formChanges = formChanges;
    }

    public SimulationMetricsResponseDTO getSimulatedResults() {
        return simulatedResults;
    }

    public void setSimulatedResults(final SimulationMetricsResponseDTO simulatedResults) {
        this.simulatedResults = simulatedResults;
    }

    public SimulationDeltaResponseDTO getDelta() {
        return delta;
    }

    public void setDelta(final SimulationDeltaResponseDTO delta) {
        this.delta = delta;
    }

    // Equals, hashCode, and toString methods
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SimulationDraftResponseDTO that = (SimulationDraftResponseDTO) o;

        if (!requestId.equals(that.requestId)) {
            return false;
        }
        if (!partyId.equals(that.partyId)) {
            return false;
        }
        if (!baseScoringId.equals(that.baseScoringId)) {
            return false;
        }
        if (!formChanges.equals(that.formChanges)) {
            return false;
        }
        if (!simulatedResults.equals(that.simulatedResults)) {
            return false;
        }
        return delta.equals(that.delta);
    }

    public int hashCode() {
        int result = requestId.hashCode();
        result = 31 * result + partyId.hashCode();
        result = 31 * result + baseScoringId.hashCode();
        result = 31 * result + formChanges.hashCode();
        result = 31 * result + simulatedResults.hashCode();
        result = 31 * result + delta.hashCode();
        return result;
    }

    public String toString() {
        return "SimulationDraftResponseDTO{" +
                "requestId='" + requestId + '\'' +
                ", partyId='" + partyId + '\'' +
                ", baseScoringId='" + baseScoringId + '\'' +
                ", formChanges=" + formChanges +
                ", simulatedResults=" + simulatedResults +
                ", delta=" + delta +
                '}';
    }
}
