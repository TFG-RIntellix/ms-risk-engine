package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.SimulationMetrics;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Input DTO for simulation save operation.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class SaveSimulationRequest {

    @NotBlank
    private String requestId;

    @NotBlank
    private String baseScoringsId;

    private String scenarioName;

    @Valid
    @NotNull
    private FormChanges formChanges;

    @Valid
    @NotNull
    private SimulationMetrics simulatedResults;

    @Valid
    @NotNull
    private SimulationDelta delta;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public String getBaseScoringsId() {
        return baseScoringsId;
    }

    public void setBaseScoringsId(final String baseScoringsId) {
        this.baseScoringsId = baseScoringsId;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(final String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public FormChanges getFormChanges() {
        return formChanges;
    }

    public void setFormChanges(final FormChanges formChanges) {
        this.formChanges = formChanges;
    }

    public SimulationMetrics getSimulatedResults() {
        return simulatedResults;
    }

    public void setSimulatedResults(final SimulationMetrics simulatedResults) {
        this.simulatedResults = simulatedResults;
    }

    public SimulationDelta getDelta() {
        return delta;
    }

    public void setDelta(final SimulationDelta delta) {
        this.delta = delta;
    }
}
