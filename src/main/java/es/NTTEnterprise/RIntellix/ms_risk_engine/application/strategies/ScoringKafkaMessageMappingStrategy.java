package es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationPayload;

/**
 * Strategy interface for mapping Kafka messages to scoring payloads for AI model.
 *
 * Moved to application.strategies because mapping is an application concern.
 */
/**
 * Core component: ScoringKafkaMessageMappingStrategy.
 * Encapsulates the logic and responsibilities assigned to this element
 * within the Hexagonal Architecture, ensuring separation of concerns.
 *
 * @author Lucía Fernández Mancebo
 * @date 28/07/2026
 */
public interface ScoringKafkaMessageMappingStrategy {

    ScoringGenerationPayload mapToRequest(Object message);

}
