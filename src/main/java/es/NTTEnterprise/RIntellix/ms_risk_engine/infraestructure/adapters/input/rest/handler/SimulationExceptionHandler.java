package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.rest.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.DuplicateSimulationNameException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.SimulationNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.SimulationPersistenceException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.SimulationValidationException;

/**
 * Global exception handler for simulation REST endpoints.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
@RestControllerAdvice
public class SimulationExceptionHandler {

    @ExceptionHandler(SimulationValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(final SimulationValidationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SimulationNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(final SimulationNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateSimulationNameException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateNameException(final DuplicateSimulationNameException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(SimulationPersistenceException.class)
    public ResponseEntity<Map<String, String>> handlePersistenceException(final SimulationPersistenceException ex) {
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(final HttpStatus status, final String message) {
        return ResponseEntity.status(status).body(Map.of("message", message));
    }
}
