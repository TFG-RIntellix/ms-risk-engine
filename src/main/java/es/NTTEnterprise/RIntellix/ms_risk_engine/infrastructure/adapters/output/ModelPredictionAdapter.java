package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.output;

import java.util.HashMap;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.ModelPredictionPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.output.dtos.ModelPredictionResponseDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.output.handler.ModelPredictionErrorHandler;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.output.util.ModelPayloadUtil;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import lombok.extern.slf4j.Slf4j;

/**
 * Output adapter that invokes the ms-model AI prediction service
 * via asynchronous, non-blocking WebClient calls.
 *
 * Responsibilities:
 * - Orchestrate WebClient POST request to the model service endpoint
 * - Perform defensive validation of inputs (fail-safe)
 * - Return CompletableFuture for async composition
 * - Delegate error handling to ModelPredictionErrorHandler
 *
 * Validation: Although the application layer validates inputs before calling
 * this adapter,
 * this adapter also validates defensively as part of the infrastructure
 * boundary.
 * This ensures robustness even if called directly or if upstream validation is
 * bypassed.
 *
 * Error Handling (delegated):
 * - 422 Unprocessable Entity: ModelValidationException
 * - 5xx Server Errors: ModelServiceException
 * - Other HTTP/network errors: propagated as-is
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-26-2026
 */
@Slf4j
@Component
public class ModelPredictionAdapter implements ModelPredictionPort {

    private final WebClient webClient;
    private final ModelPredictionErrorHandler errorHandler;

    /**
     * Constructor of the ModelPredictionAdapter class.
     *
     * @param baseUrl      the base URL of the ms-model service.
     * @param errorHandler the error handler for mapping HTTP responses to domain
     *                     exceptions.
     */
    public ModelPredictionAdapter(
            @Value("${risk.model.base-url}") final String baseUrl,
            final ModelPredictionErrorHandler errorHandler) {
        Objects.requireNonNull(baseUrl);
        Objects.requireNonNull(errorHandler);
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.errorHandler = errorHandler;
    }

    @Override
    public CompletableFuture<ModelPredictionResult> predictAsync(
            final Map<String, Object> modelPayload,
            final String requestId,
            final String modelEndpointPath) {

        try {
            ModelPayloadUtil.validatePayloadNotNull(modelPayload);
            Objects.requireNonNull(modelEndpointPath, LogMessage.ENDPOINT_PATH_NULL_ERROR);

            log.info(LogMessage.INVOKING_MODEL_PREDICTION, requestId, modelEndpointPath);

            final Map<String, Object> cleanedPayload = new HashMap<>(modelPayload);
            cleanedPayload.remove(ModelPayloadFieldNames.FIELD_EXISTING_OBLIGATIONS);

            return webClient.post()
                    .uri(modelEndpointPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(cleanedPayload)
                    .retrieve()
                    .bodyToMono(ModelPredictionResponseDTO.class)
                    .toFuture()
                    .thenApply(response -> {
                        // Convert infrastructure DTO to domain entity at the adapter boundary
                        final ModelPredictionResult domainResult = response.toDomainEntity();
                        log.info(LogMessage.MODEL_PREDICTION_COMPLETED, requestId,
                                domainResult == null ? null : domainResult.getProbabilityOfDefault());
                        return domainResult;
                    })
                    .exceptionally(throwable -> errorHandler.handleError(requestId, throwable));

        } catch (RuntimeException ex) {
            log.error(LogMessage.INVALID_MODEL_PREDICTION_REQUEST, requestId, ex.getMessage());
            return CompletableFuture.failedFuture(ex);
        }
    }
}
