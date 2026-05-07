package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output;

import java.util.Map;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;

/**
 * Output port for invoking the ms-model AI prediction service.
 * 
 * @author Lucía Fernández Mancebo
 * @Date 04-26-2026
 */
public interface ModelPredictionPort {

    /**
     * Invokes the ms-model AI prediction service.
     *
     * @param modelPayload the model input payload.
     * @param requestId    the request ID.
     * @return the model prediction result.
     */
    ModelPredictionResult predict(Map<String, Object> modelPayload, String requestId);
}
