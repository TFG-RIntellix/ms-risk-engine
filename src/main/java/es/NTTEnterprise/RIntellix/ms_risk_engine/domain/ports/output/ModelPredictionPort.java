package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;

/**
 * Output port for invoking the ms-model AI prediction service.
 * 
 * Provides asynchronous, non-blocking access to the model prediction
 * microservice.
 * All calls return a CompletableFuture to enable proper async/await composition
 * in the application layer.
 * 
 * @author Lucía Fernández Mancebo
 * @date 26/04/2026
 */
public interface ModelPredictionPort {

    /**
     * Invokes the ms-model AI prediction service asynchronously.
     * 
     * The endpoint path is passed as a separate parameter (not embedded in payload)
     * to maintain separation of concerns: payload contains model data only.
     * 
     * @param modelPayload      the model input payload (pure data, no endpoint).
     * @param requestId         the request ID for tracing.
     * @param modelEndpointPath the endpoint path for the model service.
     * @return a CompletableFuture that will be completed with the model prediction
     *         result
     *         or completed exceptionally with a ModelPredictionException subtype
     *         (ModelValidationException for 422, ModelServiceException for 5xx).
     */
    CompletableFuture<ModelPredictionResult> predictAsync(
            Map<String, Object> modelPayload,
            String requestId,
            String modelEndpointPath);
}
