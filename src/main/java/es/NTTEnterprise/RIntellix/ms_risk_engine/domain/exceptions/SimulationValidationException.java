package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when simulation request validation fails.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class SimulationValidationException extends RuntimeException {

    public SimulationValidationException(final String message) {
        super(message);
    }
}
