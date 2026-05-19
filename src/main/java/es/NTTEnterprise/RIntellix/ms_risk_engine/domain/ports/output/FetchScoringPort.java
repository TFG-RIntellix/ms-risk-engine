package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.Scoring;

/**
 * Output port for fetching scoring data from external services.
 * 
 * Provides access to base scoring information needed for simulation
 * calculations
 * and risk assessment. This port is defined at the domain layer to maintain the
 * hexagonal architecture principle.
 * 
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public interface FetchScoringPort {
    /**
     * Fetches base scoring data by request identifier.
     *
     * @param requestId the unique request identifier.
     * @return the base scoring data containing metrics and input snapshots.
     */
    Scoring fetchByRequestId(String requestId);
}
