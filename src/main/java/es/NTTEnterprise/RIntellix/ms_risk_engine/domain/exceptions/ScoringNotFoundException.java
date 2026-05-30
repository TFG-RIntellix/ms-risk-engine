package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when scoring precondition is not met for simulation.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class ScoringNotFoundException extends RuntimeException {

    public ScoringNotFoundException(final String message) {
        super(message);
    }
}
