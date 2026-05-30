package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when simulation form changes are invalid.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class InvalidFormChangesException extends RuntimeException {

    public InvalidFormChangesException(final String message, NumberFormatException ex) {
        super(message);
    }

    public InvalidFormChangesException(final String message) {
        super(message);
    }

    public InvalidFormChangesException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
