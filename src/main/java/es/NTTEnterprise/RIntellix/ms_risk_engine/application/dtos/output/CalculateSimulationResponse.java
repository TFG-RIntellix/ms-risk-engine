package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.SimulationMetrics;

/**
 * Output DTO for simulation calculation response.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class CalculateSimulationResponse {

    private SimulationMetrics simulatedResults;
    private SimulationDelta delta;

    public CalculateSimulationResponse() {
    }

    public CalculateSimulationResponse(final SimulationMetrics simulatedResults, final SimulationDelta delta) {
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
