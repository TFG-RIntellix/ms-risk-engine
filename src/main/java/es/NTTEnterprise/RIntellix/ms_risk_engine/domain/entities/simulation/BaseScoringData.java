package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation;

import java.util.Map;

/**
 * Base scoring data fetched from ms-core-data for simulation comparison.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class BaseScoringData {
    private final SimulationMetrics baseMetrics;
    private final Map<String, Object> inputSnapshot;

    public BaseScoringData(final SimulationMetrics baseMetrics, final Map<String, Object> inputSnapshot) {
        this.baseMetrics = baseMetrics;
        this.inputSnapshot = inputSnapshot;
    }

    public SimulationMetrics getBaseMetrics() {
        return baseMetrics;
    }

    public Map<String, Object> getInputSnapshot() {
        return inputSnapshot;
    }
}
