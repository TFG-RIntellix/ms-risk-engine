package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.mappers;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDraft;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CalculateSimulationDraftRequestDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.SimulationDeltaResponseDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.SimulationDraftResponseDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.SimulationMetricsResponseDTO;

@Component
public class SimulationDraftMapper {

    public FormChanges toFormChanges(final CalculateSimulationDraftRequestDTO request) {
        return new FormChanges(request == null ? null : request.getFormChanges());
    }

    public SimulationDraftResponseDTO toResponse(final SimulationDraft draft) {
        final SimulationDraftResponseDTO response = new SimulationDraftResponseDTO();
        if (draft == null) {
            return response;
        }

        response.setSimulatedResults(toMetricsResponse(draft.getSimulatedResults()));
        response.setDelta(toDeltaResponse(draft.getDelta()));
        return response;
    }

    private SimulationMetricsResponseDTO toMetricsResponse(final SimulationMetrics metrics) {
        final SimulationMetricsResponseDTO response = new SimulationMetricsResponseDTO();
        if (metrics == null) {
            return response;
        }
        response.setPd(metrics.getPd());
        response.setLgd(metrics.getLgd());
        response.setEad(metrics.getEad());
        response.setEcl(metrics.getEcl());
        response.setRiskGrade(metrics.getRiskGrade());
        response.setMonthlyPayment(metrics.getMonthlyPayment());
        response.setDti(metrics.getDti());
        response.setTotalPayment(metrics.getTotalPayment());
        response.setTotalInterest(metrics.getTotalInterest());
        response.setDisposableIncome(metrics.getDisposableIncome());
        return response;
    }

    private SimulationDeltaResponseDTO toDeltaResponse(final SimulationDelta delta) {
        final SimulationDeltaResponseDTO response = new SimulationDeltaResponseDTO();
        if (delta == null) {
            return response;
        }
        response.setPdChange(delta.getPdChange());
        response.setEclChange(delta.getEclChange());
        response.setRiskGradeChange(delta.getRiskGradeChange());
        response.setMonthlyPaymentChange(delta.getMonthlyPaymentChange());
        response.setDtiChange(delta.getDtiChange());
        return response;
    }
}
