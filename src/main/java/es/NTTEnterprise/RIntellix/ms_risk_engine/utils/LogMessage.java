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

    // Model Prediction Error Messages (Logging)
    public static final String MODEL_VALIDATION_ERROR = "Model validation failed for requestId={}. Status={}, Error: {}";
    public static final String MODEL_SERVICE_ERROR = "Model service error for requestId={}. Status={}, Error: {}";

    // Model Prediction Exception Messages (Exception Details)
    public static final String MODEL_VALIDATION_EXCEPTION_MESSAGE = "Model validation failed: ";
    public static final String MODEL_SERVICE_EXCEPTION_MESSAGE = "Model service error: ";

    // Model Prediction Adapter Messages
    public static final String INVALID_MODEL_PREDICTION_REQUEST = "Invalid model prediction request for requestId={}. Error: {}";


    // Simulation Exceptions

    public static final String SCORING_RETRIEVING_MESSAGE_ERROR = "Failed to retrieve scoring for requestId={}. Status code:{}";
    public static final String SCORING_RETRIEVING_MESSAGE_EXCEPTION = "Exception occurred while retrieving scoring for requestId={}. Error: {}";

    // ============================================================
    // VALIDATION AND EXCEPTION MESSAGES
    // ============================================================

    // Null Validation Messages
    public static final String PAYLOAD_NULL_ERROR = "modelPayload must not be null";
    public static final String ENDPOINT_PATH_NULL_ERROR = "modelEndpointPath must not be null";
    public static final String REQUEST_ID_NULL_ERROR = "requestId must not be null";
    public static final String TARGET_TYPE_NULL_ERROR = "targetType must not be null";
    public static final String KAFKA_PAYLOAD_NULL_ERROR = "Kafka payload must not be null";
    public static final String REQUEST_TYPE_CANNOT_BE_NULL = "Request type cannot be null";
    public static final String STRATEGIES_LIST_CANNOT_BE_NULL = "Strategies list cannot be null";

    // Business Rule Validation Messages
    public static final String REQUESTED_AMOUNT_MUST_BE_GREATER_THAN_ZERO = "Requested amount must be greater than 0";
    public static final String NO_RISK_CALCULATION_STRATEGY_FOUND = "No risk calculation strategy found for requestType=%s, isRevolving=%s";
    public static final String FORM_CHANGES_CONTAIN_UNSUPPORTED_FIELDS = "Form changes contain unsupported fields";

    // Utility Class Messages
    public static final String UTILITY_CLASS_NEVER_INSTANTIATE = "Utility class — never instantiate";
    public static final String FACTORY_CLASS_NEVER_INSTANTIATE = "Never instantiate";

    // ============================================================
    // DEBUG AND DIAGNOSTIC MESSAGES
    // ============================================================

    public static final String MAPPING_SCORING_ENTITY_TO_DTO = "Mapping Scoring entity to ScoringResultMessageDTO";
    public static final String ACTUAL_SCORING_DEBUG = "This is the actual scoring: {}";

}
