package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.rest.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.BaseRequestFetchException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.InvalidFormChangesException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ModelPredictionException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;

@RestControllerAdvice
public class SimulationExceptionHandler {

    @ExceptionHandler(ScoringNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleScoringNotFound(final ScoringNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(InvalidFormChangesException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFormChanges(final InvalidFormChangesException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ModelPredictionException.class)
    public ResponseEntity<Map<String, Object>> handleModelPredictionError(final ModelPredictionException ex) {
        final HttpStatus status = HttpStatus.resolve(ex.getStatusCode()) == null
                ? HttpStatus.BAD_GATEWAY
                : HttpStatus.valueOf(ex.getStatusCode());
        return ResponseEntity.status(status).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(BaseRequestFetchException.class)
    public ResponseEntity<Map<String, Object>> handleBaseRequestFetch(final BaseRequestFetchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(final MethodArgumentNotValidException ex) {
        final String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Validation error");
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
