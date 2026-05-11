package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation;

import java.util.Objects;

/**
 * Root aggregate representing the result of a simulation draft calculation.
 *
 * Contains the simulated metrics and the delta against the base scoring.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-11-2026
 */
public class SimulationDraft {

    private SimulationMetrics simulatedResults;
    private SimulationDelta delta;

    public SimulationDraft() {
    }

    public SimulationDraft(final SimulationMetrics simulatedResults,
            final SimulationDelta delta) {
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

    @Override
    public int hashCode() {
        return Objects.hash(simulatedResults, delta);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SimulationDraft other = (SimulationDraft) obj;
        return Objects.equals(simulatedResults, other.simulatedResults)
                && Objects.equals(delta, other.delta);
    }

    @Override
    public String toString() {
        return "SimulationDraft{" +
                "simulatedResults=" + simulatedResults +
                ", delta=" + delta +
                '}';
    }
}
