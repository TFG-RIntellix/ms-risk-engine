package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;

/**
 * Aggregate representing a stateless simulation draft result.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class SimulationDraft {
    // TODO: Do with simulation metrics to add more calculation metrics.
    // private SimulationMetrics simulatedResults;

    private RiskMetrics simulatedResults;
    private SimulationDelta delta;

    public SimulationDraft() {
    }

    public SimulationDraft(final RiskMetrics simulatedResults, final SimulationDelta delta) {
        this.simulatedResults = simulatedResults;
        this.delta = delta;
    }

    public RiskMetrics getSimulatedResults() {
        return simulatedResults;
    }

    public void setSimulatedResults(final RiskMetrics simulatedResults) {
        this.simulatedResults = simulatedResults;
    }

    public SimulationDelta getDelta() {
        return delta;
    }

    public void setDelta(final SimulationDelta delta) {
        this.delta = delta;
    }
}
