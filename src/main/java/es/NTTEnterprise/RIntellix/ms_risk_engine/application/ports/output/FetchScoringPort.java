package es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.output;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.BaseScoringData;

public interface FetchScoringPort {
    BaseScoringData fetchByRequestId(String requestId);
}
