package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input;

import java.util.Map;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.constraints.ValidSimulationFormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for simulation draft calculation.
 *
 * @author Lucía Fernández Mancebo
 * @date 10/05/2026
 */
public class CalculateSimulationDraftRequestDTO {

    @NotBlank(message = LogMessage.REQUEST_ID_REQUIRED_MESSAGE)
    private String requestId;

    @NotBlank(message = LogMessage.REQUEST_TYPE_REQUIRED_MESSAGE)
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
