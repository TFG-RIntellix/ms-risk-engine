package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.ModelPredictionPort;

/**
 * Application service responsible for invoking the AI model.
 *
 * It encapsulates endpoint-path enrichment and delegates model
 * execution to ModelPredictionPort.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 04-25-2026
 */
@Service
public class ScoringModelInvocationService {

    private static final String ENDPOINT_KEY = "modelEndpointPath";

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
     * Invokes prediction model for provided payload and endpoint path.
     *
     * @param modelPayload      the prepared payload for model call.
     * @param requestId         the request identifier for tracing.
     * @param modelEndpointPath the endpoint path to invoke.
     * @return the prediction result from model service.
     */
    @Async
    public CompletableFuture<ModelPredictionResult> invokePrediction(
            final Map<String, Object> modelPayload,
            final String requestId,
            final String modelEndpointPath) {
        final Map<String, Object> payloadWithEndpoint = new LinkedHashMap<>(modelPayload);
        payloadWithEndpoint.put(ENDPOINT_KEY, modelEndpointPath);
        return CompletableFuture.completedFuture(modelPredictionPort.predict(payloadWithEndpoint, requestId));
    }
}
