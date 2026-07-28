package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.input.rest.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.InvalidFormChangesException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ModelPredictionException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * REST exception handler for simulation endpoints.
 *
 * Provides consistent HTTP responses for simulation-related errors
 * without altering controller logic.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-30-2026
 */
@Slf4j
@RestControllerAdvice
public class SimulationExceptionHandler {

    @ExceptionHandler(ScoringNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleScoringNotFound(final ScoringNotFoundException ex) {
        log.warn(LogMessage.EXCEPTION_SCORING_NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(InvalidFormChangesException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFormChanges(final InvalidFormChangesException ex) {
        log.warn(LogMessage.EXCEPTION_INVALID_FORM_CHANGES, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ModelPredictionException.class)
    public ResponseEntity<Map<String, Object>> handleModelPredictionError(final ModelPredictionException ex) {
        final HttpStatus status = HttpStatus.resolve(ex.getStatusCode()) == null
                ? HttpStatus.BAD_GATEWAY
                : HttpStatus.valueOf(ex.getStatusCode());
        log.error(LogMessage.EXCEPTION_MODEL_SERVICE_ERROR, ex.getMessage(), ex);
        return ResponseEntity.status(status).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(final MethodArgumentNotValidException ex) {
        final String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Validation error");
        log.warn(LogMessage.CONTROLLER_VALIDATION_ERROR, message);
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
