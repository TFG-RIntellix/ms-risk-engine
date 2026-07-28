package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.input.kafka.strategy;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationPayload;

/**
 * Strategy interface for mapping raw Kafka messages to normalized scoring
 * generation
 * payloads.
 *
 * This allows the consumer to support multiple types of scoring generation
 * requests
 * (e.g., standard loan/mortgage, credit card) by implementing different
 * strategies
 * that can recognize and map the incoming messages accordingly.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 04-20-2026
 */

public interface ScoringGenerationMessageStrategy {

    /**
     * Indicates if strategy supports a normalized request type.
     *
     * @param requestType normalized request type
     * @return true when request type is supported
     */
    boolean supports(String requestType);

    /**
     * Converts raw payload to concrete DTO.
     *
     * @param payload raw Kafka payload
     * @return concrete request DTO instance
     */
    ScoringGenerationPayload map(Object payload);
}
