package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Simulation;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.SimulationMetrics;

/**
 * DTO sent to ms-core-data when persisting simulations.
 *
 * Extended fields can be disabled by schema feature-flag to avoid
 * compatibility issues while ms-core-data schema update is not yet deployed.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimulationPersistenceRequest {

    private String requestId;
    private String baseScoringsId;
    private String scenarioName;
    private FormChanges formChanges;
    private SimulationMetrics simulatedResults;
    private SimulationDelta delta;

    public static SimulationPersistenceRequest fromSimulation(
            final Simulation simulation,
            final boolean includeExtendedMetrics) {
        final SimulationPersistenceRequest request = new SimulationPersistenceRequest();
        request.requestId = simulation.getRequestId();
        request.baseScoringsId = simulation.getBaseScoringsId();
        request.scenarioName = simulation.getScenarioName();
        request.formChanges = simulation.getFormChanges();

        final SimulationMetrics simulationMetrics = simulation.getSimulatedResults();
        if (!includeExtendedMetrics && simulationMetrics != null) {
            final SimulationMetrics safeMetrics = new SimulationMetrics();
            safeMetrics.setPd(simulationMetrics.getPd());
            safeMetrics.setLgd(simulationMetrics.getLgd());
            safeMetrics.setEad(simulationMetrics.getEad());
            safeMetrics.setEcl(simulationMetrics.getEcl());
            safeMetrics.setRiskGrade(simulationMetrics.getRiskGrade());
            request.simulatedResults = safeMetrics;

            final SimulationDelta safeDelta = new SimulationDelta();
            safeDelta.setPdChange(simulation.getDelta() == null ? null : simulation.getDelta().getPdChange());
            safeDelta.setEcaChange(simulation.getDelta() == null ? null : simulation.getDelta().getEcaChange());
            safeDelta.setRiskGradeChange(simulation.getDelta() == null ? null : simulation.getDelta().getRiskGradeChange());
            request.delta = safeDelta;
            return request;
        }

        request.simulatedResults = simulationMetrics;
        request.delta = simulation.getDelta();
        return request;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getBaseScoringsId() {
        return baseScoringsId;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public FormChanges getFormChanges() {
        return formChanges;
    }

    public SimulationMetrics getSimulatedResults() {
        return simulatedResults;
    }

    public SimulationDelta getDelta() {
        return delta;
    }
}
