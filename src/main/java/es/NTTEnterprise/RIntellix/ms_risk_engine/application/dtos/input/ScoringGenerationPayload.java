package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input;

/**
 * Marker interface for scoring generation payloads sent to AI model.
 *
 * Different implementations represent different product types (Loan, Mortgage,
 * Credit Card).
 * Implementations are serialized to JSON for ms-model API consumption.
 *
 * @author JAVA_DEVELOPER
 * @date 2026-04-05
 */
public abstract class ScoringGenerationPayload {
    // Marker interface to identify scoring payload types

    private String requestId;
    private String requestType;

    /**
     * Default constructor for ScoringGenerationPayload.
     * 
     * @return a new instance of ScoringGenerationPayload.
     */
    public ScoringGenerationPayload() {
        // Default constructor
    }

    /**
     * Constructor for ScoringGenerationPayload with parameters.
     * 
     * @return a new instance of ScoringGenerationPayload with specified requestId
     *         and requestType.
     * @param requestId   the identifier of the request.
     * @param requestType the type of the request.
     */
    public ScoringGenerationPayload(final String requestId, final String requestType) {
        this.requestId = requestId;
        this.requestType = requestType;
    }

    // Getters and setters

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
