package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.kafka.strategy;

import java.util.Set;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CreditCardScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.mappers.ScoringKafkaRequestMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationPayload;

/**
 * Strategy for credit-card scoring generation messages.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 04-20-2026
 */
@Component
public class CreditCardScoringGenerationMessageStrategy implements ScoringGenerationMessageStrategy {

    private static final Set<String> SUPPORTED_REQUEST_TYPES = Set.of(
            "CREDIT_CARD");

    @Override
    public boolean supports(final String requestType) {
        return requestType != null && SUPPORTED_REQUEST_TYPES.contains(requestType);
    }

    @Override
    public ScoringGenerationPayload map(final Object payload) {
        return ScoringKafkaRequestMapper.toType(payload, CreditCardScoringGenerationRequest.class);
    }
}
