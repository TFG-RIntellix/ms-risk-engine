package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.input.rest.dtos;

/**
 * Core component: SimulationDraftResponse.
 * Encapsulates the logic and responsibilities assigned to this element
 * within the Hexagonal Architecture, ensuring separation of concerns.
 *
 * @author Lucía Fernández Mancebo
 * @date 28/07/2026
 */
public class SimulationDraftResponse {
    private SimulationMetricsResponse simulatedResults;
    private SimulationDeltaResponse delta;

    public SimulationMetricsResponse getSimulatedResults() {
        return simulatedResults;
    }

    public void setSimulatedResults(final SimulationMetricsResponse simulatedResults) {
        this.simulatedResults = simulatedResults;
    }

    public SimulationDeltaResponse getDelta() {
        return delta;
    }

    public void setDelta(final SimulationDeltaResponse delta) {
        this.delta = delta;
    }
}
