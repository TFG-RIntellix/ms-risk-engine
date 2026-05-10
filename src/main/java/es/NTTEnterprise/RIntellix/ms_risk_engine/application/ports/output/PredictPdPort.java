package es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.output;

import java.util.Map;

public interface PredictPdPort {
    Double predictPd(Map<String, Object> mergedVariables, String requestId);
}
