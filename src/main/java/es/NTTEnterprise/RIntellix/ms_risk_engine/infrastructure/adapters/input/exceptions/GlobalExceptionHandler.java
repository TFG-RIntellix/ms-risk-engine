package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.input.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.InvalidFormChangesException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ModelServiceException;

/**
 * Global exception handler for REST controllers.
 * Intercepts exceptions thrown across the application and formats them
 * into a standardized ApiErrorResponse payload.
 *
 * @author Lucía Fernández Mancebo
 * @date 28/07/2026
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn(LogMessage.EXCEPTION_ILLEGAL_ARGUMENT, ex.getMessage());
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ScoringNotFoundException.
     *
     * @param ex      the exception instance
     * @param request the HttpServletRequest
     * @return ResponseEntity with the error details and appropriate HTTP status
     */
    @ExceptionHandler(ScoringNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleScoringNotFound(ScoringNotFoundException ex, HttpServletRequest request) {
        log.warn(LogMessage.EXCEPTION_SCORING_NOT_FOUND, ex.getMessage());
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles InvalidFormChangesException.
     *
     * @param ex      the exception instance
     * @param request the HttpServletRequest
     * @return ResponseEntity with the error details and appropriate HTTP status
     */
    @ExceptionHandler(InvalidFormChangesException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidFormChanges(InvalidFormChangesException ex, HttpServletRequest request) {
        log.warn(LogMessage.EXCEPTION_INVALID_FORM_CHANGES, ex.getMessage());
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ModelServiceException.
     *
     * @param ex      the exception instance
     * @param request the HttpServletRequest
     * @return ResponseEntity with the error details and appropriate HTTP status
     */
    @ExceptionHandler(ModelServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleModelService(ModelServiceException ex, HttpServletRequest request) {
        log.error(LogMessage.EXCEPTION_MODEL_SERVICE_ERROR, ex.getMessage(), ex);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Error processing model request")
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error(LogMessage.EXCEPTION_UNEXPECTED, ex.getMessage(), ex);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(LogMessage.API_ERROR_UNEXPECTED_MESSAGE)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
