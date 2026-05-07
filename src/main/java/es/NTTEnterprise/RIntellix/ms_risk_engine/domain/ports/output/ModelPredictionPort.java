package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output;

import java.util.Map;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;

public interface ModelPredictionPort {

    ModelPredictionResult predict(Map<String, Object> modelPayload, String requestId);
}
