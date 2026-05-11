package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when simulation form changes are missing or invalid.
 *
 * Used to fail fast when the simulation draft request does not provide
 * the required input values or includes malformed data.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-11-2026
 */
public class InvalidFormChangesException extends RuntimeException {

    /**
     * Constructs an InvalidFormChangesException with a message.
     *
     * @param message the error message.
     */
    public InvalidFormChangesException(final String message) {
        super(message);
    }

    /**
     * Constructs an InvalidFormChangesException with a message and cause.
     *
     * @param message the error message.
     * @param cause   the underlying cause.
     */
    public InvalidFormChangesException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
