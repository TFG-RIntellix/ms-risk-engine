package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when the model service returns a 5xx server error response.
 * 
 * This indicates an internal error in the model prediction service that
 * prevented
 * successful completion of the prediction request. The error is not due to
 * client input.
 * 
 * @author Lucía Fernández Mancebo
 * @Date 05-08-2026
 */
public class ModelServiceException extends ModelPredictionException {

    /**
     * Constructs a ModelServiceException with a message and HTTP status code.
     *
     * @param message    the error message describing the service failure.
     * @param statusCode the HTTP status code (typically 500+).
     */
    public ModelServiceException(final String message, final int statusCode) {
        super(message, statusCode);
    }

    /**
     * Constructs a ModelServiceException with a message, status code, and cause.
     *
     * @param message    the error message describing the service failure.
     * @param statusCode the HTTP status code (typically 500+).
     * @param cause      the underlying cause.
     */
    public ModelServiceException(final String message, final int statusCode, final Throwable cause) {
        super(message, statusCode, cause);
    }
}
