package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.input.rest.dtos;

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
