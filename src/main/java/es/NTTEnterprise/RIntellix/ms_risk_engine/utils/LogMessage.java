package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

/**
 * Centralized log messages for the ms-core-data microservice.
 * This class provides consistent and reusable log message templates
 * to ensure uniform logging across all layers of the application.
 * 
 * @author Lucía Fernández Mancebo
 * @Date 03-01-2026
 */
public final class LogMessage {

    private LogMessage() {
        // Private constructor to prevent instantiation
    }

    // ============================================================

    public static final String KAFKA_CONSUMER_VALIDATION_REJECTED = "Kafka consumer validation rejected {}: {}";
    public static final String KAFKA_CONSUMER_ERROR = "Kafka consumer error processing record: {}";

    public static final String KAFKA_MESSAGE_RECEIVED = "Received scoring message from Kafka. key={}, topic={}, offset={}";
    public static final String KAFKA_MESSAGE_PROCESSED = "Kafka message processed successfully";
    public static final String KAFKA_MESSAGE_PROCESSING_FAILED = "Kafka message failed to process";

    public static final String REQUEST_TYPE_IS_REQUIRED = "requestType is required to resolve scoring mapping strategy";
    public static final String REQUEST_TYPE_NOT_FOUND = "No scoring mapping strategy found for type: {}";

    public static final String SCORING_PAYLOAD_NULL = "Scoring received payload is null";
    public static final String SCORING_MESSAGE_PROCESSING = "Started processing scoring message. requestId={}, requestType={}";

    public static final String STRATEGY_ERROR_PAYLOAD = "Loan/mortgage strategy requires ScoringGenerationRequest payload";
    public static final String PRE_PD_METRICS_COMPUTED = "Pre-PD metrics computed for requestId={}: ead={}, lgd={}";

    public static final String MODEL_PREDICTION_RESULT = "Model prediction received for requestId={}: pd={}";
    public static final String MODEL_PREDICTION_RESULT_TO_STRING = "Model prediction result: {}";
    public static final String FULL_METRICS_ASSEMBLED = "Full risk metrics assembled for requestId={}: pd={}, ead={}, lgd={}, ecl={}, riskGrade={}";

    public static final String MODEL_PAYLOAD_NULL = "modelPayload must not be null";
    public static final String INVOKING_MODEL_PREDICTION = "Invoking model prediction. requestId={}, endpoint={}";
    public static final String MODEL_PREDICTION_COMPLETED = "Model prediction completed. requestId={}, pd={}";
    public static final String ENDPOINT_KEY_NOT_FOUND = "modelPayload is missing required key: {}";

    public static final String ASYNCHRONOUS_MODEL_INVOCATION = "Asynchronous model invocation started for requestId={}";
    public static final String ASYNCHRONOUS_RISK_CALCULATION_STARTED = "Asynchronous risk calculation started for requestId={}";

    public static final String ERROR_ASSEMBLING_FULL_METRICS = "Error assembling full metrics for requestId={}, probability of default or pre-PD metrics are null";

    public static final String CREDIT_CARD_STRATEGY_ERROR_PAYLOAD = "Credit-card strategy requires CreditCardScoringGenerationRequest payload";

    public static final String MODEL_EXECUTION_RESULT = "Execution Result: {}";

    public static final String SCORING_PROCESSED_SUCCESSFULLY = "Scoring processed successfully. requestId={}, pd={}, riskLevel={}, explainabilityCount={}";
    public static final String ERROR_PROCESSING_SCORING_MESSAGE = "Error processing scoring message. requestId={}, requestType={}, error={}";

    public static final String PUBLISH_SCORING_NULL_PAYLOAD = "PublishScoringResult called with null scoring, skipping publish";
    public static final String SCORING_MESSAGE_PUBLISH = "Started publishing scoring message. topic={}, requestId={}";

    public static final String ERROR_PUBLISHING_SCORING_MESSAGE = "Failed to publish scoring result for requestId={}, error={}";
    public static final String SCORING_RESULT_PUBLISH_INTERRUPTED = "Scoring result publish interrupted. requestId={}";

    public static final String REQUEST_TYPE_NULL = "RequestType value cannot be null";
    public static final String UNKNOWN_REQUEST_TYPE = "Unknown RequestType value: {}";

}
