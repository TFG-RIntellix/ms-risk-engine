package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output.handler;

import java.util.concurrent.CompletionException;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ModelServiceException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ModelValidationException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Error handler for model prediction service responses.
 * 
 * Responsible for mapping HTTP status codes from the model service to
 * appropriate
 * domain exceptions. Follows the strategy pattern to decouple error handling
 * from
 * the WebClient adapter.
 * 
 * Error Mapping Strategy:
 * - 422 Unprocessable Entity: ModelValidationException (client error)
 * - 5xx Server Errors: ModelServiceException (server error)
 * - Other HTTP errors: re-throws WebClientResponseException
 * - Other exceptions: re-throws as-is or wrapped in RuntimeException
 * 
 * @author Lucía Fernández Mancebo
 * @Date 05-08-2026
 */
@Component
@Slf4j
public class ModelPredictionErrorHandler {

    /**
     * Handles errors from model prediction WebClient calls.
     * 
     * Maps HTTP status codes to domain exceptions for explicit error handling
     * in higher layers. Non-422/5xx errors propagate as-is to maintain
     * flexibility for future handling strategies.
     *
     * @param requestId the request identifier for tracing.
     * @param throwable the exception from the async WebClient call.
     * @throws ModelValidationException when HTTP 422 is received.
     * @throws ModelServiceException    when HTTP 5xx is received.
     * @throws RuntimeException         when other errors occur.
     */
    public ModelPredictionResult handleError(final String requestId, final Throwable throwable) {
        final Throwable unwrapped = unwrapCause(throwable);

        if (unwrapped instanceof WebClientResponseException) {
            handleWebClientError(requestId, (WebClientResponseException) unwrapped);
        }
        // Re-throw all other exceptions as-is
        throw unwrapped instanceof RuntimeException
                ? (RuntimeException) unwrapped
                : new RuntimeException(unwrapped);
    }

    /**
     * Handles WebClientResponseException by mapping HTTP status codes to domain
     * exceptions.
     *
     * @param requestId  the request identifier for tracing.
     * @param responseEx the WebClient response exception.
     * @throws ModelValidationException   when HTTP 422 is received.
     * @throws ModelServiceException      when HTTP 5xx is received.
     * @throws WebClientResponseException for all other HTTP status codes.
     */
    private void handleWebClientError(
            final String requestId,
            final WebClientResponseException responseEx) {

        final int statusCode = responseEx.getStatusCode().value();
        final String responseBody = responseEx.getResponseBodyAsString();

        if (statusCode == 422) {
            log.warn(LogMessage.MODEL_VALIDATION_ERROR, requestId, statusCode, responseBody);
            throw new ModelValidationException(
                    LogMessage.MODEL_VALIDATION_EXCEPTION_MESSAGE + responseBody,
                    responseEx);
        } else if (statusCode >= 500) {
            log.error(LogMessage.MODEL_SERVICE_ERROR, requestId, statusCode, responseBody);
            throw new ModelServiceException(
                    LogMessage.MODEL_SERVICE_EXCEPTION_MESSAGE + responseBody,
                    statusCode,
                    responseEx);
        } else {
            // Re-throw other HTTP errors as-is (3xx, 4xx except 422)
            throw responseEx;
        }
    }

    /**
     * Unwraps the cause from a CompletionException if present.
     * 
     * CompletionException is thrown by CompletableFuture when an exception
     * occurs in a completion stage. This unwraps it to get the original exception.
     *
     * @param throwable the throwable that may be wrapped.
     * @return the root cause if wrapped in CompletionException, otherwise the
     *         original throwable.
     */
    private Throwable unwrapCause(final Throwable throwable) {
        if (throwable instanceof CompletionException) {
            return throwable.getCause() != null ? throwable.getCause() : throwable;
        }
        return throwable;
    }
}
