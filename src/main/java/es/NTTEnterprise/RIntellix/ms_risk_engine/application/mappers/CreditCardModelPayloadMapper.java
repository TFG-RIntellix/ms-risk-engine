package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CreditCardScoringGenerationRequest;

/**
 * Mapper for transforming credit card scoring requests into
 * the temporary payload contract required by ms-model API.
 *
 * Credit card request body is currently a placeholder until
 * final API contract is defined.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 04-25-2026
 */
@Component
public class CreditCardModelPayloadMapper {

    /**
     * Maps credit card generation request to current placeholder payload.
     *
     * @param request               the source credit card scoring request.
     * @param normalizedRequestType the normalized request type.
     * @return the model payload to send to credit card prediction endpoint.
     */
    public Map<String, Object> toModelPayload(
            final CreditCardScoringGenerationRequest request,
            final String normalizedRequestType) {
        final Map<String, Object> modelPayload = new LinkedHashMap<>();
        modelPayload.put("request_type", normalizedRequestType);
        modelPayload.put("request_id", request.getRequestId());
        return modelPayload;
    }
}
