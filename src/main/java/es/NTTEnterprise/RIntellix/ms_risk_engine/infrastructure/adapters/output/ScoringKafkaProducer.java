package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.output;

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
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.ScoringResultPublisherPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
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
 * @date 26/04/2026
 */
@Slf4j
@Component
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
            log.info(LogMessage.SCORING_PAYLOAD_NULL);
            return;
        }

        final String requestId = scoring.getRequestId();
        log.info(LogMessage.SCORING_RESULT_PUBLISH_START, topic, requestId);

        final ScoringResultMessageDTO dto = scoringResultMessageDTOMapper.toDTO(scoring);

        final Message<?> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, requestId)
                .build();

        try {
            kafkaTemplate.send(message).get();
            log.info(LogMessage.SCORING_RESULT_PUBLISH_SUCCESS, requestId);
        } catch (ExecutionException ex) {
            log.error(LogMessage.ERROR_PUBLISHING_SCORING_MESSAGE, requestId, ex.getMessage(), ex);
            throw new RuntimeException(LogMessage.ERROR_PUBLISHING_SCORING_MESSAGE + requestId, ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error(LogMessage.SCORING_RESULT_PUBLISH_INTERRUPTED, requestId, ex);
            throw new RuntimeException(LogMessage.SCORING_RESULT_PUBLISH_INTERRUPTED + requestId, ex);
        }
    }
}
