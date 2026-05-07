package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.ScoringResultMessageDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.ScoringResultMessageDTOMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.output.ScoringResultPublisherPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Scoring;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka producer adapter that publishes scoring results to the
 * PersistScoring topic for ms-core-data to consume and persist.
 *
 * Implements the application-level output port, converting the
 * Scoring domain entity into a transport DTO and sending it
 * as a Kafka message with requestId as the message key.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-26-2026
 */
@Component
@Slf4j
public class ScoringKafkaProducer implements ScoringResultPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ScoringResultMessageDTOMapper scoringResultMessageDTOMapper;
    private final String topic;

    /**
     * Constructor of the ScoringKafkaProducer class.
     *
     * @param kafkaTemplate                 the Kafka template for publishing
     *                                      messages.
     * @param scoringResultMessageDTOMapper the mapper for converting Scoring to
     *                                      output DTO.
     * @param topic                         the target Kafka topic name.
     */
    public ScoringKafkaProducer(
            final KafkaTemplate<String, Object> kafkaTemplate,
            final ScoringResultMessageDTOMapper scoringResultMessageDTOMapper,
            @Value("${scoring.kafka.topic.persist}") final String topic) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
        this.scoringResultMessageDTOMapper = Objects.requireNonNull(scoringResultMessageDTOMapper);
        this.topic = Objects.requireNonNull(topic);
    }

    @Override
    public void publishScoringResult(final Scoring scoring) {
        if (scoring == null) {
            log.warn("publishScoringResult called with null scoring, skipping publish");
            return;
        }

        final String requestId = scoring.getRequestId();
        log.info("Publishing scoring result to Kafka. topic={}, requestId={}", topic, requestId);

        final ScoringResultMessageDTO dto = scoringResultMessageDTOMapper.toDTO(scoring);

        final Message<?> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, requestId)
                .build();

        try {
            kafkaTemplate.send(message).get();
            log.info("Scoring result published successfully. requestId={}", requestId);
        } catch (ExecutionException ex) {
            log.error("Failed to publish scoring result. requestId={}, error={}",
                    requestId, ex.getMessage(), ex);
            throw new RuntimeException("Failed to publish scoring result for requestId=" + requestId, ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Scoring result publish interrupted. requestId={}", requestId, ex);
            throw new RuntimeException("Scoring result publish interrupted for requestId=" + requestId, ex);
        }
    }
}
