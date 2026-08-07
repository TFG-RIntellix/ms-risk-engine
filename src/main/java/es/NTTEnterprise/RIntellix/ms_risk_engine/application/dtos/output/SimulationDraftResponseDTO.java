package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output;

import java.util.Map;

/**
 * DTO for representing the response of a simulation draft,
 * containing the simulated results, deltas, and other relevant information.
 *
 * 
 */
/**
 * Core component: SimulationDraftResponseDTO.
 * Encapsulates the logic and responsibilities assigned to this element
 * within the Hexagonal Architecture, ensuring separation of concerns.
 *
 * @author Lucía Fernández Mancebo
 * @date 28/07/2026
 */
public class SimulationDraftResponseDTO {

    private Map<String, Object> formChanges;
    private SimulationMetricsResponseDTO simulatedResults;
    private SimulationDeltaResponseDTO delta;

    /**
     * Default constructor for SimulationDraftResponseDTO.
     * 
     * @return A new instance of SimulationDraftResponseDTO with default values.
     */
    public SimulationDraftResponseDTO() {
    }

    /**
     * Constructor for SimulationDraftResponseDTO.
     * 
     * @param requestId        Reference to the associated scoring request
     * @param partyId          Reference to the associated party (customer)
     * @param baseScoringId    Reference to the base scoring used for the simulation
     * @param formChanges      The changes made to the input features for the
     *                         simulation
     * @param simulatedResults The resulting risk metrics from the simulation
     * @param delta            The computed deltas (PD, EL, risk grade) compared to
     *                         the original scoring
     */
    public SimulationDraftResponseDTO(Map<String, Object> formChanges, SimulationMetricsResponseDTO simulatedResults,
            SimulationDeltaResponseDTO delta, String simulatedDecision) {
        this.formChanges = formChanges;
        this.simulatedResults = simulatedResults;
        this.delta = delta;
    }

    // Getters and Setters

    public Map<String, Object> getFormChanges() {
        return formChanges;
    }

    public void setFormChanges(final Map<String, Object> formChanges) {
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

        if (!formChanges.equals(that.formChanges)) {
            return false;
        }
        if (!simulatedResults.equals(that.simulatedResults)) {
            return false;
        }
        return delta.equals(that.delta);
    }

    public int hashCode() {
        int result = formChanges.hashCode();
        result = 31 * result + simulatedResults.hashCode();
        result = 31 * result + delta.hashCode();
        return result;
    }

    public String toString() {
        return "SimulationDraftResponseDTO{" +
                ", formChanges=" + formChanges +
                ", simulatedResults=" + simulatedResults +
                ", delta=" + delta +
                '}';
    }
}
