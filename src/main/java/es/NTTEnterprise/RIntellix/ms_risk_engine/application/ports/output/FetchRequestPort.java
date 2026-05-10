package es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.output;

import java.util.Map;

public interface FetchRequestPort {
    Map<String, Object> fetchByRequestId(String requestId);
}
