package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationPayload;

/**
 * Strategy interface for mapping Kafka messages to scoring payloads for AI
 * model.
 *
 * Each implementation handles mapping from a specific message type
 * (ScoringGenerationDTO or CreditCardScoringGenerationDTO) into a type-specific
 * ScoringGenerationPayload DTO for ms-model API consumption.
 *
 * @author Lucía Fernández Mancebo
 * @date 2026-04-05
 */
public interface ScoringKafkaMessageMappingStrategy {

    /**
     * Maps a Kafka message Object to a ScoringGenerationPayload DTO.
     *
     * Different implementations handle different message types:
     * - LoanScoringKafkaMessageMappingStrategy (ScoringGenerationDTO) →
     * LoanScoringPayloadDTO
     * - CreditCardScoringKafkaMessageMappingStrategy
     * (CreditCardScoringGenerationDTO) → CreditCardScoringPayloadDTO
     *
     * @param message the Kafka message (will be cast to expected type by
     *                implementation)
     * @return the mapped ScoringGenerationPayload (implementation-specific DTO)
     */
    ScoringGenerationPayload mapToRequest(Object message);

}
