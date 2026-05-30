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
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ScoringNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleScoringNotFound(ScoringNotFoundException ex,
                        HttpServletRequest request) {
                log.warn(LogMessage.EXCEPTION_SCORING_NOT_FOUND, ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponseDTO("SCORING_NOT_FOUND", ex.getMessage(), 404,
                                                LocalDateTime.now()));
        }

        @ExceptionHandler(InvalidFormChangesException.class)
        public ResponseEntity<ErrorResponseDTO> handleInvalidFormChanges(InvalidFormChangesException ex,
                        HttpServletRequest request) {
                log.warn(LogMessage.EXCEPTION_INVALID_FORM_CHANGES, ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorResponseDTO("INVALID_FORM_CHANGES", ex.getMessage(), 400,
                                                LocalDateTime.now()));
        }

        @ExceptionHandler(ModelServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handleModelService(ModelServiceException ex,
                        HttpServletRequest request) {
                log.error(LogMessage.EXCEPTION_MODEL_SERVICE_ERROR, ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponseDTO("MODEL_ERROR", "Error processing model request", 500,
                                                LocalDateTime.now()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, HttpServletRequest request) {
                log.error(LogMessage.EXCEPTION_UNEXPECTED, ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponseDTO("INTERNAL_ERROR", "An unexpected error occurred", 500,
                                                LocalDateTime.now()));
        }

}
