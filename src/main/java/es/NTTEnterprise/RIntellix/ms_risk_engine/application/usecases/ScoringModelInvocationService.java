package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;




import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.ModelPredictionPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;

/**
 * Application service responsible for invoking the AI model asynchronously.
 *
 * It delegates model execution to the ModelPredictionPort, passing the payload
 * and endpoint as separate parameters for clean separation of concerns.
 * The  annotation ensures the method runs in a separate thread pool,
 * allowing true non-blocking composition.
 *
 * @author Lucía Fernández Mancebo
 * @date 25/04/2026
 */

public class ScoringModelInvocationService {

    private final ModelPredictionPort modelPredictionPort;

    /**
     * Constructor of the ScoringModelInvocationService class.
     *
     * @param modelPredictionPort the output port used to call ms-model API.
     */
    public ScoringModelInvocationService(final ModelPredictionPort modelPredictionPort) {
        this.modelPredictionPort = Objects.requireNonNull(modelPredictionPort);
    }

    /**
     * Invokes the prediction model asynchronously for the provided payload and
     * endpoint.
     *
     * @param modelPayload      the payload containing pure model data (no
     *                          endpoint).
     * @param requestId         the request identifier for tracing.
     * @param modelEndpointPath the endpoint path to invoke.
     * @return a CompletableFuture that completes with the prediction result,
     *         or completes exceptionally with ModelPredictionException subtype
     *         if the model service returns an error response.
     * @throws IllegalArgumentException if payload or endpoint is null.
     */
    
    public CompletableFuture<ModelPredictionResult> invokePrediction(
            final Map<String, Object> modelPayload,
            final String requestId,
            final String modelEndpointPath) {
        // Validate early at application layer - fail fast
        Objects.requireNonNull(modelPayload, LogMessage.PAYLOAD_NULL_ERROR);
        Objects.requireNonNull(modelEndpointPath, LogMessage.ENDPOINT_PATH_NULL_ERROR);
        Objects.requireNonNull(requestId, LogMessage.REQUEST_ID_NULL_ERROR);

        return modelPredictionPort.predictAsync(modelPayload, requestId, modelEndpointPath);
    }
}
