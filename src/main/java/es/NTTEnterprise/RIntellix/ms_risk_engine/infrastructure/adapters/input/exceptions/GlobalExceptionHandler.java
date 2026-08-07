package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.input.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import feign.FeignException;

import java.time.LocalDateTime;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.InvalidFormChangesException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ModelServiceException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ModelPredictionException;

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
        public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
                        HttpServletRequest request) {
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
        public ResponseEntity<ApiErrorResponse> handleScoringNotFound(ScoringNotFoundException ex,
                        HttpServletRequest request) {
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
        public ResponseEntity<ApiErrorResponse> handleInvalidFormChanges(InvalidFormChangesException ex,
                        HttpServletRequest request) {
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
        public ResponseEntity<ApiErrorResponse> handleModelService(ModelServiceException ex,
                        HttpServletRequest request) {
                log.error(LogMessage.EXCEPTION_MODEL_SERVICE_ERROR, ex.getMessage(), ex);
                ApiErrorResponse error = ApiErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                                .message(LogMessage.EXCEPTION_MODEL_PROCESSING_ERROR)
                                .path(request.getRequestURI())
                                .build();
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /**
         * Handles ModelPredictionException.
         *
         * @param ex      the exception instance
         * @param request the HttpServletRequest
         * @return ResponseEntity with the error details and appropriate HTTP status
         */
        @ExceptionHandler(ModelPredictionException.class)
        public ResponseEntity<ApiErrorResponse> handleModelPredictionError(ModelPredictionException ex,
                        HttpServletRequest request) {
                final HttpStatus status = HttpStatus.resolve(ex.getStatusCode()) == null
                                ? HttpStatus.BAD_GATEWAY
                                : HttpStatus.valueOf(ex.getStatusCode());
                log.error(LogMessage.EXCEPTION_MODEL_SERVICE_ERROR, ex.getMessage(), ex);
                ApiErrorResponse error = ApiErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .error(status.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();
                return new ResponseEntity<>(error, status);
        }

        /**
         * Handles MethodArgumentNotValidException.
         *
         * @param ex      the exception instance
         * @param request the HttpServletRequest
         * @return ResponseEntity with the error details and appropriate HTTP status
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                final String message = ex.getBindingResult().getFieldErrors().stream()
                                .map(FieldError::getDefaultMessage)
                                .findFirst()
                                .orElse(LogMessage.EXCEPTION_VALIDATION_ERROR_DEFAULT);
                log.warn(LogMessage.CONTROLLER_VALIDATION_ERROR, message);
                ApiErrorResponse error = ApiErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message(message)
                                .path(request.getRequestURI())
                                .build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        /**
         * Handles HttpMessageNotReadableException.
         * 
         * @param ex      the exception instance
         * @param request the HttpServletRequest
         * @return ResponseEntity with the error details and appropriate HTTP status
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                        HttpServletRequest request) {
                log.warn(LogMessage.EXCEPTION_MALFORMED_JSON_LOG, ex.getMessage());
                ApiErrorResponse error = ApiErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message(LogMessage.EXCEPTION_MALFORMED_JSON_MESSAGE)
                                .path(request.getRequestURI())
                                .build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        /**
         * Handles FeignException.
         * 
         * @param ex      the exception instance
         * @param request the HttpServletRequest
         * @return ResponseEntity with the error details and appropriate HTTP status
         */
        @ExceptionHandler(FeignException.class)
        public ResponseEntity<ApiErrorResponse> handleFeignException(FeignException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.resolve(ex.status());
                if (status == null) {
                        status = HttpStatus.INTERNAL_SERVER_ERROR;
                }
                log.warn(LogMessage.EXCEPTION_FEIGN_CLIENT_ERROR, ex.getMessage());

                // If it's a 404 from scoring retrieval, we can provide a cleaner message
                String message = ex.status() == 404 ? LogMessage.EXCEPTION_FEIGN_SCORING_NOT_FOUND : ex.getMessage();

                ApiErrorResponse error = ApiErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .error(status.getReasonPhrase())
                                .message(message)
                                .path(request.getRequestURI())
                                .build();
                return new ResponseEntity<>(error, status);
        }

        /**
         * Handles Exception.
         * 
         * @param ex      the exception instance
         * @param request the HttpServletRequest
         * @return ResponseEntity with the error details and appropriate HTTP status
         */
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
