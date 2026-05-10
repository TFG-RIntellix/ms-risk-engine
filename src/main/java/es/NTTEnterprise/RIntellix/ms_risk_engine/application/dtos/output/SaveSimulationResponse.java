package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output;

/**
 * Output DTO for simulation persistence response.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class SaveSimulationResponse {

    private String id;
    private String scenarioName;

    public SaveSimulationResponse() {
    }

    public SaveSimulationResponse(final String id, final String scenarioName) {
        this.id = id;
        this.scenarioName = scenarioName;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(final String scenarioName) {
        this.scenarioName = scenarioName;
    }
}
