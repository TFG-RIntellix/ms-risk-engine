package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.rest;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CalculateSimulationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.SaveSimulationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.CalculateSimulationResponse;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.SaveSimulationResponse;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases.CalculateSimulationUseCase;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases.SaveSimulationUseCase;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Simulation;

import jakarta.validation.Valid;

/**
 * REST controller for simulation operations.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
@RestController
@Validated
@RequestMapping("/api/v1/simulations")
public class SimulationController {

    private final CalculateSimulationUseCase calculateSimulationUseCase;
    private final SaveSimulationUseCase saveSimulationUseCase;

    /**
     * Constructor of the SimulationController class.
     *
     * @param calculateSimulationUseCase calculate use case.
     * @param saveSimulationUseCase      save use case.
     */
    public SimulationController(
            final CalculateSimulationUseCase calculateSimulationUseCase,
            final SaveSimulationUseCase saveSimulationUseCase) {
        this.calculateSimulationUseCase = Objects.requireNonNull(calculateSimulationUseCase);
        this.saveSimulationUseCase = Objects.requireNonNull(saveSimulationUseCase);
    }

    /**
     * Calculates simulation metrics from form changes.
     *
     * @param request calculate request.
     * @return calculated simulation result.
     */
    @PostMapping("/calculate")
    public ResponseEntity<CalculateSimulationResponse> calculateSimulation(
            @Valid @RequestBody final CalculateSimulationRequest request) {
        return ResponseEntity.ok(calculateSimulationUseCase.calculateSimulation(request));
    }

    /**
     * Saves a simulation scenario.
     *
     * @param request save request.
     * @return created simulation response.
     */
    @PostMapping
    public ResponseEntity<SaveSimulationResponse> saveSimulation(
            @Valid @RequestBody final SaveSimulationRequest request) {
        final Simulation persistedSimulation = saveSimulationUseCase.saveSimulation(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SaveSimulationResponse(persistedSimulation.getId(), persistedSimulation.getScenarioName()));
    }
}
