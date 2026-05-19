package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CalculateSimulationDraftRequestDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.SimulationDraftResponseDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases.CalculateSimulationDraftUseCase;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDraft;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.mappers.SimulationDraftMapper;
import jakarta.validation.Valid;

/**
 * REST controller for handling simulation draft calculations.
 *
 * This controller provides an endpoint to calculate a simulation draft based on
 * user-provided form changes. It validates the input request, invokes the
 * business logic to compute the draft, and returns the result in a structured
 * response format.
 * 
 * @author Lucía Fernández Mancebo
 * @Date 03-15-2026
 */
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

    /**
     * Endpoint to calculate a simulation draft based on user-provided form changes.
     * 
     * @param request the request containing the form changes and request ID for
     *                correlation
     * @return a ResponseEntity containing the calculated SimulationDraftResponseDTO
     */
    @PostMapping("/draft")
    public ResponseEntity<SimulationDraftResponseDTO> calculateDraft(
            @Valid @RequestBody final CalculateSimulationDraftRequestDTO request) {
        final SimulationDraft draft = calculateSimulationDraftUseCase.calculateDraft(
                request.getRequestId(), request.getRequestType(),
                simulationDraftMapper.toFormChanges(request));
        return ResponseEntity.ok(simulationDraftMapper.toApiResponse(draft));
    }
}
