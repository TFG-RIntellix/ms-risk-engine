package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Simulation;

/**
 * Output port to retrieve and persist simulation data in ms-core-data.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public interface SimulationRepositoryPort {

    /**
     * Retrieves base scoring by request id.
     *
     * @param requestId the request identifier.
     * @return the base scoring.
     */
    Scoring fetchBaseScoringByRequestId(String requestId);

    /**
     * Checks whether scenario name already exists for the request.
     *
     * @param requestId     the request identifier.
     * @param scenarioName  the scenario name.
     * @return true if name already exists.
     */
    boolean existsScenarioName(String requestId, String scenarioName);

    /**
     * Persists simulation in ms-core-data.
     *
     * @param simulation the simulation to persist.
     * @return persisted simulation.
     */
    Simulation saveSimulation(Simulation simulation);
}
