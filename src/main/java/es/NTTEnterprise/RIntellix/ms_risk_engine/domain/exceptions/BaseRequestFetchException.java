package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when base request data cannot be retrieved.
 *
 * Used by adapters and use cases to report failures when calling
 * ms-core-data for the request snapshot required by simulations.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-11-2026
 */
public class BaseRequestFetchException extends RuntimeException {

    /**
     * Constructs a BaseRequestFetchException with a message.
     *
     * @param message the error message.
     */
    public BaseRequestFetchException(final String message) {
        super(message);
    }

    /**
     * Constructs a BaseRequestFetchException with a message and cause.
     *
     * @param message the error message.
     * @param cause   the underlying cause.
     */
    public BaseRequestFetchException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
