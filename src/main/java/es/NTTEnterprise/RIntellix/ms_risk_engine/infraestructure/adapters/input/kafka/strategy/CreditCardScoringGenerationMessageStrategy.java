package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.kafka.strategy;

import java.util.Set;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CreditCardScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationPayload;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.dtos.CreditCardScoringGenerationDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.mappers.ScoringKafkaRequestMapper;

/**
 * Strategy for credit-card scoring generation messages.
 *
 * Maps the generic Kafka payload first to {@link CreditCardScoringGenerationDTO}
 * (which ignores unknown fields such as education, occupationSector, etc.)
 * and then converts it into the application-layer
 * {@link CreditCardScoringGenerationRequest}.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 04-20-2026
 */
@Component
public class CreditCardScoringGenerationMessageStrategy implements ScoringGenerationMessageStrategy {

    private static final Set<String> SUPPORTED_REQUEST_TYPES = Set.of(
            "TARJETA_CREDITO");

    /**
     * Determines if this strategy supports the given request type.
     *
     * @param requestType the request type to check
     * @return true if the request type is for credit cards, false otherwise
     */
    @Override
    public boolean supports(final String requestType) {
        return requestType != null && SUPPORTED_REQUEST_TYPES.contains(requestType);
    }

    /**
     * Maps the incoming Kafka payload to a ScoringGenerationPayload for credit
     * cards.
     * First converts to the infrastructure DTO (ignoring unrelated fields),
     * then maps to the application-layer request.
     *
     * @param payload the generic object payload from the Kafka message
     * @return the correctly mapped ScoringGenerationPayload
     */
    @Override
    public ScoringGenerationPayload map(final Object payload) {
        final CreditCardScoringGenerationDTO dto = ScoringKafkaRequestMapper.toType(payload,
                CreditCardScoringGenerationDTO.class);
        return ScoringKafkaRequestMapper.toType(dto, CreditCardScoringGenerationRequest.class);
    }
}
