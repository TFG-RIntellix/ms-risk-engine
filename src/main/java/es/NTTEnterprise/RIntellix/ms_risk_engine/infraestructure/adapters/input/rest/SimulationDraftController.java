package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases.CalculateSimulationDraftUseCase;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDraft;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.rest.dtos.CalculateSimulationDraftRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.rest.dtos.SimulationDraftResponse;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.mappers.SimulationDraftMapper;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1/simulations")
public class SimulationDraftController {

    private final CalculateSimulationDraftUseCase calculateSimulationDraftUseCase;
    private final SimulationDraftMapper simulationDraftMapper;

    public SimulationDraftController(
            final CalculateSimulationDraftUseCase calculateSimulationDraftUseCase,
            final SimulationDraftMapper simulationDraftMapper) {
        this.calculateSimulationDraftUseCase = calculateSimulationDraftUseCase;
        this.simulationDraftMapper = simulationDraftMapper;
    }

    @PostMapping("/draft")
    public ResponseEntity<SimulationDraftResponse> calculateDraft(
            @Valid @RequestBody final CalculateSimulationDraftRequest request) {
        final SimulationDraft draft = calculateSimulationDraftUseCase.calculateDraft(
                request.getRequestId(),
                simulationDraftMapper.toFormChanges(request));
        return ResponseEntity.ok(simulationDraftMapper.toResponse(draft));
    }
}
