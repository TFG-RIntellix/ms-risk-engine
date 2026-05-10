package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.SaveSimulationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Simulation;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.DuplicateSimulationNameException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.SimulationValidationException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.SimulationRepositoryPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;

/**
 * Use case for simulation persistence.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
@Service
public class SaveSimulationUseCase {

    private static final String DEFAULT_SCENARIO_PREFIX = "Simulación";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final SimulationRepositoryPort simulationRepositoryPort;

    /**
     * Constructor of the SaveSimulationUseCase class.
     *
     * @param simulationRepositoryPort simulation repository output port.
     */
    public SaveSimulationUseCase(final SimulationRepositoryPort simulationRepositoryPort) {
        this.simulationRepositoryPort = Objects.requireNonNull(simulationRepositoryPort);
    }

    /**
     * Persists simulation after validating name uniqueness.
     *
     * @param request save request.
     * @return persisted simulation.
     */
    public Simulation saveSimulation(final SaveSimulationRequest request) {
        validateSaveRequest(request);

        final String scenarioName = resolveScenarioName(request.getScenarioName());

        if (simulationRepositoryPort.existsScenarioName(request.getRequestId(), scenarioName)) {
            throw new DuplicateSimulationNameException(LogMessage.SIMULATION_DUPLICATED_NAME);
        }

        final Simulation simulation = new Simulation();
        simulation.setRequestId(request.getRequestId());
        simulation.setBaseScoringsId(request.getBaseScoringsId());
        simulation.setScenarioName(scenarioName);
        simulation.setCreatedAt(new Date());
        simulation.setFormChanges(request.getFormChanges());
        simulation.setSimulatedResults(request.getSimulatedResults());
        simulation.setDelta(request.getDelta());

        return simulationRepositoryPort.saveSimulation(simulation);
    }

    private void validateSaveRequest(final SaveSimulationRequest request) {
        if (request == null) {
            throw new SimulationValidationException(LogMessage.SIMULATION_SAVE_REQUEST_REQUIRED);
        }
        if (request.getRequestId() == null || request.getRequestId().isBlank()) {
            throw new SimulationValidationException(LogMessage.SIMULATION_REQUEST_ID_REQUIRED);
        }
        if (request.getBaseScoringsId() == null || request.getBaseScoringsId().isBlank()) {
            throw new SimulationValidationException(LogMessage.SIMULATION_BASE_SCORING_ID_REQUIRED);
        }
        if (request.getFormChanges() == null || request.getSimulatedResults() == null || request.getDelta() == null) {
            throw new SimulationValidationException(LogMessage.SIMULATION_SAVE_DATA_REQUIRED);
        }
    }

    private String resolveScenarioName(final String scenarioName) {
        if (scenarioName == null || scenarioName.isBlank()) {
            return DEFAULT_SCENARIO_PREFIX + " "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        }
        return scenarioName.trim();
    }
}
