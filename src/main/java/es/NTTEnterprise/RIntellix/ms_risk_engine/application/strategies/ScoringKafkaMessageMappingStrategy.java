package es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationPayload;

/**
 * Strategy interface for mapping Kafka messages to scoring payloads for AI model.
 *
 * Moved to application.strategies because mapping is an application concern.
 */
public interface ScoringKafkaMessageMappingStrategy {

    ScoringGenerationPayload mapToRequest(Object message);

}
