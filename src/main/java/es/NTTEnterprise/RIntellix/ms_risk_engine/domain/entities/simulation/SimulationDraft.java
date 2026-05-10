package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation;

/**
 * Aggregate representing a stateless simulation draft result.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class SimulationDraft {
    private SimulationMetrics simulatedResults;
    private SimulationDelta delta;

    public SimulationDraft() {
    }

    public SimulationDraft(final SimulationMetrics simulatedResults, final SimulationDelta delta) {
        this.simulatedResults = simulatedResults;
        this.delta = delta;
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
}
