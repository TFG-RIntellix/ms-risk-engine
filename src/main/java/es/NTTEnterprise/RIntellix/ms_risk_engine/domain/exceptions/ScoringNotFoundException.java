package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when a base scoring cannot be retrieved for a request.
 *
 * This is used by the simulation flow to indicate that the base scoring
 * required for delta calculations is missing or unavailable.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-11-2026
 */
public class ScoringNotFoundException extends RuntimeException {

    /**
     * Constructs a ScoringNotFoundException with a message.
     *
     * @param message the error message.
     */
    public ScoringNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructs a ScoringNotFoundException with a message and cause.
     *
     * @param message the error message.
     * @param cause   the underlying cause.
     */
    public ScoringNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
