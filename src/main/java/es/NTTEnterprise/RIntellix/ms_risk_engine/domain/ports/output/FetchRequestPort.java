package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output;

import java.util.Map;

/**
 * Output port for fetching request data from external services.
 * 
 * Provides access to base request information needed for simulation calculations.
 * This port is defined at the domain layer to maintain the hexagonal architecture
 * principle where the domain does not depend on the application layer.
 * 
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public interface FetchRequestPort {

    /**
     * Fetches base request data by request identifier.
     *
     * @param requestId the unique request identifier.
     * @return a map containing the request data, or null if not found.
     */
    Map<String, Object> fetchByRequestId(String requestId);
}
