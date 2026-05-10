package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.mappers;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDraft;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.rest.dtos.CalculateSimulationDraftRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.rest.dtos.SimulationDeltaResponse;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.rest.dtos.SimulationDraftResponse;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.rest.dtos.SimulationMetricsResponse;

@Component
public class SimulationDraftMapper {

    public FormChanges toFormChanges(final CalculateSimulationDraftRequest request) {
        return new FormChanges(request == null ? null : request.getFormChanges());
    }

    public SimulationDraftResponse toResponse(final SimulationDraft draft) {
        final SimulationDraftResponse response = new SimulationDraftResponse();
        if (draft == null) {
            return response;
        }

        response.setSimulatedResults(toMetricsResponse(draft.getSimulatedResults()));
        response.setDelta(toDeltaResponse(draft.getDelta()));
        return response;
    }

    private SimulationMetricsResponse toMetricsResponse(final SimulationMetrics metrics) {
        final SimulationMetricsResponse response = new SimulationMetricsResponse();
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

    private SimulationDeltaResponse toDeltaResponse(final SimulationDelta delta) {
        final SimulationDeltaResponse response = new SimulationDeltaResponse();
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
