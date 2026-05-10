package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.input.rest.dtos;

import java.util.Map;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.constraints.ValidSimulationFormChanges;
import jakarta.validation.constraints.NotBlank;

public class CalculateSimulationDraftRequest {

    @NotBlank(message = "requestId is required")
    private String requestId;

    @ValidSimulationFormChanges
    private Map<String, Object> formChanges;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public Map<String, Object> getFormChanges() {
        return formChanges;
    }

    public void setFormChanges(final Map<String, Object> formChanges) {
        this.formChanges = formChanges;
    }
}
