package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.FormChanges;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Input DTO for simulation calculation.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public class CalculateSimulationRequest {

    @NotBlank
    private String requestId;

    @Valid
    @NotNull
    private FormChanges formChanges;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public FormChanges getFormChanges() {
        return formChanges;
    }

    public void setFormChanges(final FormChanges formChanges) {
        this.formChanges = formChanges;
    }
}
