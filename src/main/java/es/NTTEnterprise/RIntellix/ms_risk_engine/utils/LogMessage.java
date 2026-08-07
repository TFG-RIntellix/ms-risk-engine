package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

/**
 * Centralized log messages for the ms-risk-engine microservice.
 * This class provides consistent and reusable log message templates
 * to ensure uniform logging across all layers of the application.
 * 
 * @author Lucía Fernández Mancebo
 * @date 01/03/2026
 */
public final class LogMessage {

        public static final String UTILITY_CLASS_NEVER_INSTANTIATE = "Never instantiate";

        private LogMessage() {
                throw new UnsupportedOperationException(UTILITY_CLASS_NEVER_INSTANTIATE);
        }

        // ============================================================

        public static final String KAFKA_CONSUMER_VALIDATION_REJECTED = "Kafka consumer validation rejected {}: {}";
        public static final String KAFKA_CONSUMER_ERROR = "Kafka consumer error processing record: {}";

        public static final String KAFKA_MESSAGE_RECEIVED = "Received scoring message from Kafka. key={}, topic={}, offset={}";
        public static final String KAFKA_MESSAGE_PROCESSED = "Kafka message processed successfully";
        public static final String KAFKA_MESSAGE_PROCESSING_FAILED = "Kafka message failed to process";

        public static final String REQUEST_TYPE_IS_REQUIRED = "requestType is required to resolve scoring mapping strategy";
        public static final String REQUEST_TYPE_NOT_FOUND = "No scoring mapping strategy found for type: %s";

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
        public static final String RISK_METRICS_CALCULATION_STARTED = "Risk metrics calculation started for requestId={}";

        public static final String ERROR_ASSEMBLING_FULL_METRICS = "Error assembling full metrics for requestId={}, probability of default or pre-PD metrics are null";

        public static final String CREDIT_CARD_STRATEGY_ERROR_PAYLOAD = "Credit-card strategy requires CreditCardScoringGenerationRequest payload";

        public static final String MODEL_EXECUTION_RESULT_CREDIT_CARD = "Credit Card Execution Result: Scoring completed with PD: {}";
        public static final String MODEL_EXECUTION_RESULT_LOAN_MORTGAGE = "Loan/Mortgage Execution Result: Scoring completed with PD: {}";

        public static final String SCORING_PROCESSED_SUCCESSFULLY = "Scoring processed successfully. requestId={}, pd={}, riskLevel={}, explainabilityCount={}";
        public static final String SCORING_EXECUTION_TIME = "Scoring execution completed in {} ms for requestId={}";
        public static final String ERROR_PROCESSING_SCORING_MESSAGE = "Error processing scoring message. requestId={}, requestType={}, error={}";

        public static final String CONTROLLER_VALIDATION_ERROR = "Validation error: {}";

        public static final String PUBLISH_SCORING_NULL_PAYLOAD = "PublishScoringResult called with null scoring, skipping publish";
        public static final String SCORING_RESULT_PUBLISH_START = "Started publishing scoring result. topic={}, requestId={}";
        public static final String SCORING_RESULT_PUBLISH_SUCCESS = "Scoring result published. requestId={}";

        public static final String ERROR_PUBLISHING_SCORING_MESSAGE = "Failed to publish scoring result for requestId={}, error={}";
        public static final String SCORING_RESULT_PUBLISH_INTERRUPTED = "Scoring result publish interrupted. requestId={}";

        public static final String REQUEST_TYPE_NULL = "RequestType value cannot be null";
        public static final String UNKNOWN_REQUEST_TYPE = "Unknown RequestType value: {}";

        public static final String RISK_METRICS_HARD_CUTOFF_TRIGGERED = "Hard cutoff rule triggered for request {}, bypassing model call";
        public static final String EXCEPTION_EMPTY_MODEL_RESPONSE = "Empty model response for requestId: %s";
        public static final String EXCEPTION_PD_VALUE_MISSING = "PD value missing in model response for requestId: %s";
        public static final String EXCEPTION_FAILED_TO_PREDICT_PD = "Failed to predict PD for requestId: %s";

        // Model Prediction Error Messages (Logging)
        public static final String MODEL_VALIDATION_ERROR = "Model validation failed for requestId={}. Status={}, Error: {}";
        public static final String MODEL_SERVICE_ERROR = "Model service error for requestId={}. Status={}, Error: {}";

        // Model Prediction Exception Messages (Exception Details)
        public static final String MODEL_VALIDATION_EXCEPTION_MESSAGE = "Model validation failed: ";
        public static final String MODEL_SERVICE_EXCEPTION_MESSAGE = "Model service error: ";

        // Model Prediction Adapter Messages
        public static final String INVALID_MODEL_PREDICTION_REQUEST = "Invalid model prediction request for requestId={}. Error: {}";

        // Simulation Exceptions

        public static final String SCORING_RETRIEVING_MESSAGE_ERROR = "Failed to retrieve scoring for requestId=%s. Status code:%s";
        public static final String SCORING_RETRIEVING_MESSAGE_EXCEPTION = "Exception occurred while retrieving scoring for requestId=%s. Error: %s";
        public static final String SCORING_RETRIEVING_INPUT_EMPTY = "Scoring input snapshot is missing for requestId: %s";

        // Simulation Draft
        public static final String SIMULATION_BASE_SCORING_RETRIEVED = "Retrieved base scoring for requestId {}: {}";
        public static final String SIMULATION_BASE_SCORING_NULL = "Base scoring is null";
        public static final String SIMULATION_FORM_CHANGES_REQUIRED = "Form changes are required for requestId: %s";
        public static final String SIMULATION_REQUEST_DTO_NULL = "Request DTO is null, returning empty FormChanges";
        public static final String SIMULATION_DRAFT_NULL = "SimulationDraft is null, returning empty response";
        public static final String SIMULATION_DRAFT_MAPPED = "Successfully mapped SimulationDraft to API response";
        public static final String SIMULATION_METRICS_NULL = "SimulationMetrics is null, returning empty metrics response";
        public static final String SIMULATION_DELTA_NULL = "SimulationDelta is null, returning empty delta response";

        // Financial Metrics Calculation
        public static final String FINANCIAL_METRICS_CALCULATION_START = "Calculating financial metrics - Principal: {}, Rate: {}%, Term: {} months, Annual Income: {}, Existing Obligations: {}";
        public static final String FINANCIAL_METRICS_CALCULATION_COMPLETE = "Financial metrics calculated - Monthly Payment: {}, DTI: {}, Total Payment: {}, Total Interest: {}, Disposable Income: {}";
        public static final String FINANCIAL_METRICS_ATTACHED = "Financial metrics calculated and attached - Monthly Payment: {}, DTI: {}";

        // Global Exception Handling
        public static final String EXCEPTION_SCORING_NOT_FOUND = "Scoring not found: {}";
        public static final String EXCEPTION_INVALID_FORM_CHANGES = "Invalid form changes: {}";
        public static final String EXCEPTION_MODEL_SERVICE_ERROR = "Model service error: {}";
        public static final String EXCEPTION_UNEXPECTED = "Unexpected error: {}";
        public static final String EXCEPTION_ILLEGAL_ARGUMENT = "Illegal argument: {}";
        public static final String API_ERROR_UNEXPECTED_MESSAGE = "An unexpected error occurred. Please try again later.";
        public static final String EXCEPTION_FEIGN_CLIENT_ERROR = "Feign client error: {}";
        public static final String EXCEPTION_FEIGN_SCORING_NOT_FOUND = "Scoring data not found";
        public static final String EXCEPTION_MALFORMED_JSON_LOG = "Malformed JSON request or empty body: {}";
        public static final String EXCEPTION_MALFORMED_JSON_MESSAGE = "Malformed JSON request or empty body";
        public static final String EXCEPTION_MODEL_PROCESSING_ERROR = "Error processing model request";
        public static final String EXCEPTION_VALIDATION_ERROR_DEFAULT = "Validation error";

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
        public static final String LOAN_PAYMENT_CALCULATOR_CANNOT_BE_NULL = "LoanPaymentCalculator cannot be null";
        public static final String MODEL_PAYLOAD_UTILITIES_CANNOT_BE_NULL = "ModelPayloadUtilities cannot be null";
        public static final String ENUM_NORMALIZER_CANNOT_BE_NULL = "EnumNormalizer cannot be null";
        public static final String BOOLEAN_CONVERTER_CANNOT_BE_NULL = "BooleanConverter cannot be null";
        public static final String BASE_VARIABLES_CANNOT_BE_NULL = "Base variables cannot be null";
        public static final String MODEL_PREDICTION_PORT_CANNOT_BE_NULL = "ModelPredictionPort cannot be null";
        public static final String RISK_GRADE_CALCULATOR_CANNOT_BE_NULL = "RiskGradeCalculator cannot be null";
        public static final String FINANCIAL_METRICS_CALCULATION_SERVICE_CANNOT_BE_NULL = "FinancialMetricsCalculationService cannot be null";
        public static final String DTI_CALCULATION_SERVICE_CANNOT_BE_NULL = "DtiCalculationService cannot be null";
        public static final String RISK_METRICS_CONTEXT_CANNOT_BE_NULL = "RiskMetricsCalculationContext cannot be null";
        public static final String PROBABILITY_OF_DEFAULT_CANNOT_BE_NULL = "Probability of default cannot be null";
        public static final String PRINCIPAL_AMOUNT_CANNOT_BE_NULL = "Principal amount cannot be null";
        public static final String ANNUAL_INTEREST_RATE_CANNOT_BE_NULL = "Annual interest rate cannot be null";
        public static final String TERM_MONTHS_CANNOT_BE_NULL = "Term months cannot be null";
        public static final String ANNUAL_INCOME_CANNOT_BE_NULL = "Annual income cannot be null";
        public static final String EXISTING_OBLIGATIONS_CANNOT_BE_NULL = "Existing obligations cannot be null";
        public static final String NAMING_CONVERTER_CANNOT_BE_NULL = "NamingConverter cannot be null";

        // Business Rule Validation Messages
        public static final String REQUESTED_AMOUNT_MUST_BE_GREATER_THAN_ZERO = "Requested amount must be greater than 0";
        public static final String NO_RISK_CALCULATION_STRATEGY_FOUND = "No risk calculation strategy found for requestType=%s, isRevolving=%s";
        public static final String FORM_CHANGES_CONTAIN_UNSUPPORTED_FIELDS = "Form changes contain unsupported fields";
        public static final String REQUEST_ID_REQUIRED_MESSAGE = "requestId is required";
        public static final String REQUEST_TYPE_REQUIRED_MESSAGE = "requestType is required";

        public static final String FACTORY_CLASS_NEVER_INSTANTIATE = "Never instantiate";

        // ============================================================
        // DEBUG AND DIAGNOSTIC MESSAGES
        // ============================================================

        public static final String MAPPING_SCORING_ENTITY_TO_DTO = "Mapping Scoring entity to ScoringResultMessageDTO";
        public static final String ACTUAL_SCORING_DEBUG = "This is the actual scoring: {}";
        public static final String REQUEST_TYPE_NULL_ERROR = "Request type is null, cannot resolve strategy";

        // ============================================================
        // SIMULATION CALCULATION MESSAGES
        // ============================================================

        public static final String SIMULATIONDRAFT_PETITION_RECEIVED = "Starting to calculate simulation draft for requestId: {} & requestType: {}";
        public static final String SIMULATION_DTI_RECALCULATED = "Recalculated DTI for simulation: {} (monthlyPayment={}, annualIncome={})";
        public static final String SIMULATION_LTV_RECALCULATED = "Recalculated LTV for mortgage simulation: {} (loanAmount={}, propertyValue={})";
        public static final String SIMULATION_PROPERTY_VALUE_REMOVED = "Removed propertyValue from model input (it is not a model feature, LTV is used instead)";
        public static final String SIMULATION_RISK_INDICATORS_CALCULATION_ERROR = "Error recalculating risk indicators for simulation. Error: {}";
        public static final String CC_FINANCIAL_METRICS_CALCULATION_START = "Calculating credit card financial metrics...";
        public static final String CC_REVOLVING_SIMULATION_COMPLETE = "Revolving simulation complete. Iterations={}, Balance={}, TotalInterest={}, TotalPayment={}";
        public static final String CC_DTI_RECALCULATED = "Recalculated credit card DTI for simulation: {} (monthlyPayment={}, annualIncome={})";
        public static final String FINANCIAL_METRICS_STRATEGIES_CANNOT_BE_NULL = "Financial metrics strategies cannot be null";
        public static final String NO_FINANCIAL_METRICS_STRATEGY_FOUND = "No financial metrics strategy found for requestType=%s, isRevolving=%s";

        // ============================================================
        // HARD-CUTOFF RULE LOG MESSAGES
        // ============================================================

        /** Logged at WARN level when DTI hard-cutoff fires before AI model call. */
        public static final String SCORING_HARD_CUTOFF_DTI = "Hard-cutoff triggered: DTI={} exceeds 50%% threshold for requestId={}. Skipping AI model.";

        /** Logged at WARN level when LTV hard-cutoff fires before AI model call. */
        public static final String SCORING_HARD_CUTOFF_LTV = "Hard-cutoff triggered: LTV={} exceeds 80%% threshold for requestId={}. Skipping AI model.";

        /** Logged at WARN level when LTI hard-cutoff fires before AI model call. */
        public static final String SCORING_HARD_CUTOFF_LTI = "Hard-cutoff triggered: LTI={} exceeds 40%% threshold for requestId={}. Skipping AI model.";

        /** Logged at INFO level after a hard-cutoff scoring has been published. */
        public static final String SCORING_HARD_CUTOFF_PERSISTED = "Hard-cutoff scoring published for requestId={}. EAD={}, LGD={}, ECL={}";

        // ============================================================
        // KAFKA MESSAGES
        // ============================================================
        public static final String RECEIVED_REQUEST = "Received request from ms-core-data: {}";

}
