package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when scenario name already exists.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class DuplicateSimulationNameException extends RuntimeException {

    public DuplicateSimulationNameException(final String message) {
        super(message);
    }
}
