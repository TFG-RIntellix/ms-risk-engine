package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when the base request retrieval fails.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class BaseRequestFetchException extends RuntimeException {

    public BaseRequestFetchException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
