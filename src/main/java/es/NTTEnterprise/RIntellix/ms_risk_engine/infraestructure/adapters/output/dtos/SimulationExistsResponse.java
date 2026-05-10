package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output.dtos;

/**
 * DTO wrapper for scenario-name uniqueness validation response.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class SimulationExistsResponse {

    private boolean exists;

    public boolean isExists() {
        return exists;
    }

    public void setExists(final boolean exists) {
        this.exists = exists;
    }
}
