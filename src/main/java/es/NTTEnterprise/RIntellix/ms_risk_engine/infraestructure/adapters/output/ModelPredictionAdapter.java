package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.ModelPredictionPort;
import lombok.extern.slf4j.Slf4j;

/**
 * Output adapter that invokes the ms-model AI prediction service
 * via synchronous WebClient calls.
 *
 * Extracts the target endpoint path from the payload, sends the
 * model input as a JSON POST request, and maps the response into
 * a ModelPredictionResult domain entity.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-26-2026
 */
@Component
@Slf4j
public class ModelPredictionAdapter implements ModelPredictionPort {

    private static final String ENDPOINT_KEY = "modelEndpointPath";

    private final WebClient webClient;

    /**
     * Constructor of the ModelPredictionAdapter class.
     *
     * @param baseUrl the base URL of the ms-model service.
     */
    public ModelPredictionAdapter(
            @Value("${risk.model.base-url}") final String baseUrl) {
        Objects.requireNonNull(baseUrl);
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public ModelPredictionResult predict(final Map<String, Object> modelPayload, final String requestId) {
        if (modelPayload == null) {
            throw new IllegalArgumentException("modelPayload must not be null");
        }

        final String endpointPath = extractEndpointPath(modelPayload);
        modelPayload.remove(ENDPOINT_KEY);
        log.info("Invoking model prediction. requestId={}, endpoint={}", requestId, endpointPath);

        final ModelPredictionResult result = webClient.post()
                .uri(endpointPath)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(modelPayload)
                .retrieve()
                .bodyToMono(ModelPredictionResult.class)
                .block();

        log.info("Model prediction completed. requestId={}, pd={}",
                requestId,
                result == null ? null : result.getProbabilityOfDefault());
        return result;
    }

    private String extractEndpointPath(final Map<String, Object> modelPayload) {
        final Object endpointValue = modelPayload.get(ENDPOINT_KEY);
        if (endpointValue == null) {
            throw new IllegalArgumentException("modelPayload is missing required key: " + ENDPOINT_KEY);
        }
        return String.valueOf(endpointValue);
    }
}
