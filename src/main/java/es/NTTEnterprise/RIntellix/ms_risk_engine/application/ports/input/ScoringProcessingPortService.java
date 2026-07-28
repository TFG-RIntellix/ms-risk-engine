package es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.input;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationPayload;

/**
 * Input port for Kafka scoring consumer processing operations.
 * Defines the use case contract for processing incoming scoring
 * computation messages and persisting their results.
 * 
 * @author Lucía Fernández Mancebo
 * @date 21/03/2026
 */
public interface ScoringProcessingPortService {

    /**
     * Processes a scoring generation request message received from Kafka, sent by
     * ms-core-data microservice.
     * This method handles the core business logic of validating the incoming
     * message, mapping it to the appropriate
     * domain request, calling the proper model to calculate the PD, and by strategy
     * pattern calculate the metrics and the final scoring result
     * based on the type of scoring request (loan/mortgage vs credit card). Finally,
     * it creates a ScoringResult entity and sends via kafka to be persisted in
     * MongoDB by ms-core-data.
     * 
     * @param scoringMessage the incoming message payload containing the
     *                       scoring generation request details, which can
     *                       be of different types (ScoringGenerationRequest
     *                       for loans/mortgages or
     *                       CreditCardScoringGenerationRequest for credit
     *                       cards)
     *                       following the Strategy Pattern for
     *                       type-specific processing.
     * @return true if the message was processed successfully and the scoring result
     *         was sent for persistence, false otherwise.
     */
    boolean processScoringMessage(ScoringGenerationPayload scoringMessage);
}
