package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when base scoring does not exist for simulation generation.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class SimulationNotFoundException extends RuntimeException {

    public SimulationNotFoundException(final String message) {
        super(message);
    }
}
