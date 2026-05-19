package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input;

import java.util.Map;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.constraints.ValidSimulationFormChanges;
import jakarta.validation.constraints.NotBlank;

public class CalculateSimulationDraftRequestDTO {

    @NotBlank(message = "requestId is required")
    private String requestId;

    @NotBlank(message = "requestType is required")
    private String requestType;

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

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
