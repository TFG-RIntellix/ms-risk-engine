package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.input;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDraft;

/**
 * Input port for simulation draft calculation use case.
 * 
 * Defines the contract for calculating simulation drafts based on
 * form changes while maintaining separation between the application layer
 * and external adapters in a hexagonal architecture.
 * 
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public interface SimulationDraftPortService {

    /**
     * Calculates a simulation draft based on form changes.
     *
     * @param requestId   the request identifier for fetching base data.
     * @param requestType the type of the request.
     * @param formChanges the user-specified form modifications.
     * @return the calculated simulation draft with metrics and deltas.
     */
    SimulationDraft calculateDraft(String requestId, String requestType, FormChanges formChanges);
}
