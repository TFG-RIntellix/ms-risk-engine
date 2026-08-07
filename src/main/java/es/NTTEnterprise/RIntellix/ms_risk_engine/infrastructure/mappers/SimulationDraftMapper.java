package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.mappers;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CalculateSimulationDraftRequestDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.SimulationDeltaResponseDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.SimulationDraftResponseDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.SimulationMetricsResponseDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDraft;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Infrastructure Adapter Mapper for Simulation Draft API Response.
 *
 * Hexagonal Architecture Role:
 * - Implements the OUTPUT adapter pattern
 * - Converts domain-level calculated results (SimulationDraft) into external
 * API response DTOs
 * - Bridges the boundary between internal business logic and external clients
 * - Responsible for API contract formatting and external representation only
 *
 * Responsibilities:
 * - Map domain FormChanges to input DTO (inbound adapter)
 * - Map domain SimulationDraft results to API response DTO (outbound adapter)
 * - Aggregate domain metrics and deltas into response format
 * - Handle null safety and type conversions at API boundary
 *
 * Separation of Concerns:
 * - INPUT Mapping: CalculateSimulationDraftRequestDTO → FormChanges
 * (application concern)
 * - OUTPUT Mapping: SimulationDraft → SimulationDraftResponseDTO
 * (infrastructure concern)
 * - Domain calculations remain in application/domain layers
 * - API contract formatting remains in infrastructure layer
 *
 * @author Lucía Fernández Mancebo
 * @date 05/09/2026
 */
@Slf4j
@Component
public class SimulationDraftMapper {

    /**
     * Maps input API request DTO to domain FormChanges entity.
     * This is an inbound adapter conversion from external API contract to internal
     * domain model.
     *
     * @param request the input request DTO from API client
     * @return FormChanges domain entity containing user form changes, or empty
     *         FormChanges if request is null
     */
    public FormChanges toFormChanges(final CalculateSimulationDraftRequestDTO request) {
        if (request == null) {
            log.warn(LogMessage.SIMULATION_REQUEST_DTO_NULL);
            return new FormChanges(null);
        }
        return new FormChanges(request.getFormChanges());
    }

    /**
     * Maps domain SimulationDraft calculated results to external API response DTO.
     * This is an outbound adapter conversion from internal domain model to external
     * API contract.
     *
     * Orchestrates the conversion of nested domain entities:
     * - SimulationDraft → SimulationDraftResponseDTO (top-level)
     * - SimulationMetrics → SimulationMetricsResponseDTO (calculated metrics)
     * - SimulationDelta → SimulationDeltaResponseDTO (metric changes)
     *
     * @param draft the domain SimulationDraft containing calculated simulation
     *              results
     * @return SimulationDraftResponseDTO ready for API response, or empty response
     *         if draft is null
     */
    public SimulationDraftResponseDTO toApiResponse(final SimulationDraft draft) {
        final SimulationDraftResponseDTO response = new SimulationDraftResponseDTO();

        if (draft == null) {
            log.warn(LogMessage.SIMULATION_DRAFT_NULL);
            return response;
        }

        // Delegate nested mappings to preserve single responsibility
        response.setSimulatedResults(mapMetricsToDto(draft.getSimulatedResults()));
        response.setDelta(mapDeltaToDto(draft.getDelta()));
        response.setFormChanges(draft.getFormChanges().getValues());

        if (log.isDebugEnabled()) {
            log.debug(LogMessage.SIMULATION_DRAFT_MAPPED);
        }

        return response;
    }

    /**
     * Maps domain SimulationMetrics to response DTO.
     * Encapsulates the detail of metrics attribute transformation.
     *
     * Handles null safety by returning empty DTO if metrics is null,
     * ensuring API clients always receive a valid response structure.
     *
     * @param metrics the domain metrics entity containing calculated risk
     *                indicators
     * @return SimulationMetricsResponseDTO with all metric values, or empty if
     *         metrics is null
     */
    private SimulationMetricsResponseDTO mapMetricsToDto(final RiskMetrics metrics) {
        final SimulationMetricsResponseDTO response = new SimulationMetricsResponseDTO();

        if (metrics == null) {
            log.warn(LogMessage.SIMULATION_METRICS_NULL);
            return response;
        }

        // Map all metric attributes to response DTO
        response.setPd(metrics.getProbabilityOfDefault());
        response.setLgd(metrics.getLossGivenDefault());
        response.setEad(metrics.getExposureAtDefault());
        response.setEcl(metrics.getExpectedCalculatedLoss());
        response.setRiskGrade(metrics.getRiskLevel());

        // Map financial metrics if available
        if (metrics.getFinancialMetrics() != null) {
            final var fm = metrics.getFinancialMetrics();
            response.setMonthlyPayment(fm.getMonthlyPayment() != null ? fm.getMonthlyPayment() : 0.0);
            response.setDti(fm.getDebtToIncomeRatio() != null ? fm.getDebtToIncomeRatio() : 0.0);
            response.setTotalPayment(fm.getTotalPayment() != null ? fm.getTotalPayment() : 0.0);
            response.setTotalInterest(fm.getTotalInterest() != null ? fm.getTotalInterest() : 0.0);
            response.setDisposableIncome(
                    fm.getMonthlyDisposableIncome() != null ? fm.getMonthlyDisposableIncome() : 0.0);
        } else {
            // Default to 0.0 if no financial metrics available
            response.setMonthlyPayment(0.0);
            response.setDti(0.0);
            response.setTotalPayment(0.0);
            response.setTotalInterest(0.0);
            response.setDisposableIncome(0.0);
        }

        return response;
    }

    /**
     * Maps domain SimulationDelta to response DTO.
     * Encapsulates the detail of delta (metric changes) transformation.
     *
     * The delta represents the differences between base scenario and simulated
     * scenario,
     * allowing API clients to understand the impact of form changes on risk
     * metrics.
     *
     * @param delta the domain delta entity containing metric changes
     * @return SimulationDeltaResponseDTO with all change values, or empty if delta
     *         is null
     */
    private SimulationDeltaResponseDTO mapDeltaToDto(final SimulationDelta delta) {
        final SimulationDeltaResponseDTO response = new SimulationDeltaResponseDTO();

        if (delta == null) {
            log.warn(LogMessage.SIMULATION_DELTA_NULL);
            return response;
        }

        // Map all delta attributes to response DTO
        response.setPdChange(delta.getPdChange());
        response.setLgdChange(delta.getLgdChange());
        response.setEadChange(delta.getEadChange());
        response.setEclChange(delta.getEclChange());
        response.setRiskGradeChange(delta.getRiskGradeChange());
        response.setMonthlyPaymentChange(delta.getMonthlyPaymentChange());
        response.setDtiChange(delta.getDtiChange());
        response.setTotalPaymentChange(delta.getTotalPaymentChange());
        response.setTotalInterestChange(delta.getTotalInterestChange());
        response.setMonthlyDisposableIncomeChange(delta.getMonthlyDisposableIncomeChange());

        return response;
    }
}
