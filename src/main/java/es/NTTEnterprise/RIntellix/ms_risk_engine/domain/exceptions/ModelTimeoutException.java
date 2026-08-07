package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when a request to the model prediction service times out.
 * 
 * This indicates that the model service did not respond within the configured
 * timeout period.
 * The model service may be overloaded or experiencing connectivity issues.
 * 
 * @author Lucía Fernández Mancebo
 * @date 08/05/2026
 */
public class ModelTimeoutException extends ModelPredictionException {

    private static final int TIMEOUT_STATUS = 504; // Gateway Timeout

    /**
     * Constructs a ModelTimeoutException with a message.
     *
     * @param message the error message describing the timeout.
     */
    public ModelTimeoutException(final String message) {
        super(message, TIMEOUT_STATUS);
    }

    /**
     * Constructs a ModelTimeoutException with a message and cause.
     *
     * @param message the error message describing the timeout.
     * @param cause   the underlying cause (typically TimeoutException).
     */
    public ModelTimeoutException(final String message, final Throwable cause) {
        super(message, TIMEOUT_STATUS, cause);
    }
}
