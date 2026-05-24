package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.config;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.ErrorResponseDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.InvalidFormChangesException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ModelServiceException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(ScoringNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleScoringNotFound(ScoringNotFoundException ex,
                        HttpServletRequest request) {
                log.warn("Scoring not found: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponseDTO("SCORING_NOT_FOUND", ex.getMessage(), 404,
                                                LocalDateTime.now()));
        }

        @ExceptionHandler(InvalidFormChangesException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidFormChanges(InvalidFormChangesException ex,
                        HttpServletRequest request) {
                log.warn("Invalid form changes: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorResponseDTO("INVALID_FORM_CHANGES", ex.getMessage(), 400,
                                                LocalDateTime.now()));
        }

        @ExceptionHandler(ModelServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handleModelService(ModelServiceException ex,
                        HttpServletRequest request) {
                log.error("Model service error: {}", ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponseDTO("MODEL_ERROR", "Error processing model request", 500,
                                                LocalDateTime.now()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, HttpServletRequest request) {
                log.error("Unexpected error: {}", ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponseDTO("INTERNAL_ERROR", "An unexpected error occurred", 500,
                                                LocalDateTime.now()));
        }

}
