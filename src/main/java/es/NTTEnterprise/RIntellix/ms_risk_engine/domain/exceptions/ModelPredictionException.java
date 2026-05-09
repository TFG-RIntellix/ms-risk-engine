package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Base exception for model prediction-related errors.
 * 
 * This exception serves as the root for all prediction service failures,
 * allowing callers to catch all model-related errors generically or
 * handle specific subtypes with fine-grained control.
 * 
 * @author Lucía Fernández Mancebo
 * @Date 05-08-2026
 */
public class ModelPredictionException extends RuntimeException {

    private final int statusCode;

    /**
     * Constructs a ModelPredictionException with a message and HTTP status code.
     *
     * @param message    the error message.
     * @param statusCode the HTTP status code from the model service.
     */
    public ModelPredictionException(final String message, final int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Constructs a ModelPredictionException with a message, cause, and HTTP status
     * code.
     *
     * @param message    the error message.
     * @param statusCode the HTTP status code from the model service.
     * @param cause      the underlying cause.
     */
    public ModelPredictionException(final String message, final int statusCode, final Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Gets the HTTP status code from the model service response.
     *
     * @return the HTTP status code.
     */
    public int getStatusCode() {
        return statusCode;
    }
}
