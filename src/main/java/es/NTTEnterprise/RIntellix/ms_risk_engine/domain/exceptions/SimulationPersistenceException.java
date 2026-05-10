package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when simulation persistence integration fails.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class SimulationPersistenceException extends RuntimeException {

    public SimulationPersistenceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
