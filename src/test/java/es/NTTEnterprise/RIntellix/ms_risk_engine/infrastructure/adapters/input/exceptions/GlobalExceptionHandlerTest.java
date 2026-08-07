package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.input.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.InvalidFormChangesException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ModelPredictionException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Test
    @DisplayName("Should handle ScoringNotFoundException with 404")
    void testHandleScoringNotFound() {
        ScoringNotFoundException ex = new ScoringNotFoundException("req-1");
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleScoringNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("req-1", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle InvalidFormChangesException with 400")
    void testHandleInvalidFormChanges() {
        InvalidFormChangesException ex = new InvalidFormChangesException("Invalid fields");
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleInvalidFormChanges(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Invalid fields", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle ModelPredictionException with appropriate status")
    void testHandleModelPredictionError() {
        ModelPredictionException ex = new ModelPredictionException("Model error", 422);
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleModelPredictionError(ex, request);

        assertEquals(422, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Unprocessable Content", response.getBody().getError());
        assertEquals("Model error", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with 400")
    void testHandleValidation() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("dto", "field", "Must not be null");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleValidation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Must not be null", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with 400")
    void testHandleHttpMessageNotReadable() {
        org.springframework.http.HttpInputMessage inputMessage = mock(org.springframework.http.HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error", inputMessage);
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleHttpMessageNotReadable(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals(LogMessage.EXCEPTION_MALFORMED_JSON_MESSAGE, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle FeignException NotFound with 404")
    void testHandleFeignExceptionNotFound() {
        feign.FeignException ex = feign.FeignException.errorStatus("GET /api", 
            feign.Response.builder()
                .request(feign.Request.create(feign.Request.HttpMethod.GET, "/api", java.util.Collections.emptyMap(), null, java.nio.charset.StandardCharsets.UTF_8, null))
                .status(404)
                .reason("Not Found")
                .headers(java.util.Collections.emptyMap())
                .build());
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleFeignException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals(LogMessage.EXCEPTION_FEIGN_SCORING_NOT_FOUND, response.getBody().getMessage());
    }
}
