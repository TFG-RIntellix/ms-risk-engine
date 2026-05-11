package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Scoring;

/**
 * Output port for publishing scoring results to the ms-core-data.
 *
 * Implementations are responsible for serializing the Scoring domain entity
 * into a transport-level DTO and delivering it to the configured Kafka topic,
 * so that ms-core-data can persist the scoring result in MongoDB.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-26-2026
 */
public interface ScoringResultPublisherPort {

    /**
     * Publishes a fully computed scoring result.
     *
     * @param scoring the scoring domain entity to publish.
     */
    void publishScoringResult(Scoring scoring);
}

