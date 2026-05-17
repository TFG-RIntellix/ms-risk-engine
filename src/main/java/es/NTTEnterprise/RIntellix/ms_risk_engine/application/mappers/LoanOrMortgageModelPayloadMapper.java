package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.utils.ModelPayloadUtilities;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;

/**
 * Mapper for transforming loan and mortgage scoring requests into
 * the Spanish field contract required by ms-model API.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 04-25-2026
 */
@Component
public class LoanOrMortgageModelPayloadMapper {

    private static final String YES_VALUE = "Si";
    private static final String NO_VALUE = "No";
    // TODO: Default LTV applied when ms-core-data sends null. Should be resolved in
    // ms-core-data.
    private static final double DEFAULT_LTV = 0.0;

    private final ModelPayloadUtilities payloadUtilities;

    public LoanOrMortgageModelPayloadMapper(final ModelPayloadUtilities payloadUtilities) {
        this.payloadUtilities = Objects.requireNonNull(payloadUtilities,
                LogMessage.MODEL_PAYLOAD_UTILITIES_CANNOT_BE_NULL);
    }

    /**
     * Maps loan or mortgage generation request to model payload.
     *
     * @param request the source scoring generation request.
     * @return the model payload with Spanish field names.
     */
    public Map<String, Object> toModelPayload(final ScoringGenerationRequest request) {
        final Map<String, Object> modelPayload = new LinkedHashMap<>();
        modelPayload.put(ModelPayloadFieldNames.FIELD_EDAD, request.getAge());
        // TODO: Enum normalization applied below — ms-core-data should send values
        // matching ms-model enums directly.
        modelPayload.put(ModelPayloadFieldNames.FIELD_GENERO,
                payloadUtilities.normalizeEnumToTitleCase(request.getGender()));
        modelPayload.put(ModelPayloadFieldNames.FIELD_ESTADO_CIVIL,
                payloadUtilities.normalizeEnumToTitleCase(request.getMaritalStatus()));
        // Educacion enums use spaces: "Sin Estudios", "Formacion Profesional"
        modelPayload.put(ModelPayloadFieldNames.FIELD_EDUCACION,
                payloadUtilities.normalizeEnumToTitleCaseWithSpaces(request.getEducation()));
        modelPayload.put(ModelPayloadFieldNames.FIELD_SITUACION_LABORAL,
                payloadUtilities.normalizeEnumToTitleCase(request.getEmploymentStatus()));
        // Sector trabajo enums use spaces: "Sector Publico"
        modelPayload.put(ModelPayloadFieldNames.FIELD_SECTOR_TRABAJO,
                payloadUtilities.normalizeEnumToTitleCaseWithSpaces(request.getOccupationSector()));
        modelPayload.put(ModelPayloadFieldNames.FIELD_DEPENDIENTES, request.getDependents());
        modelPayload.put(ModelPayloadFieldNames.FIELD_VIVIENDA,
                payloadUtilities.normalizeEnumToTitleCase(request.getHomeOwnership()));
        modelPayload.put(ModelPayloadFieldNames.FIELD_TIENE_HIPOTECA,
                Boolean.TRUE.equals(request.getHasMortgage()) ? YES_VALUE : NO_VALUE);
        modelPayload.put(ModelPayloadFieldNames.FIELD_INGRESOS_ANUALES, request.getAnnualIncome());
        // TODO: tipo_prestamo defaults to "Personal" — ms-core-data should provide the
        // specific loan type enum value.
        modelPayload.put(ModelPayloadFieldNames.FIELD_TIPO_PRESTAMO, request.getLoanType());
        modelPayload.put(ModelPayloadFieldNames.FIELD_PROPOSITO,
                payloadUtilities.normalizeEnumToTitleCase(request.getPurpose()));
        modelPayload.put(ModelPayloadFieldNames.FIELD_MONTO_PRESTAMO, request.getLoanAmount());
        modelPayload.put(ModelPayloadFieldNames.FIELD_PLAZO_MESES, request.getTermMonths());
        modelPayload.put(ModelPayloadFieldNames.FIELD_TASA_INTERES, request.getInterestRate());
        // TODO: null LTV defaulted to 0.0 — ms-core-data should compute and provide
        // this value.
        modelPayload.put(ModelPayloadFieldNames.FIELD_LTV, request.getLtv() != null ? request.getLtv() : DEFAULT_LTV);
        modelPayload.put(ModelPayloadFieldNames.FIELD_DTI, request.getDti());
        modelPayload.put(ModelPayloadFieldNames.FIELD_NUM_PRESTAMOS_PREVIOS, request.getPreviousLoansCount());
        modelPayload.put(ModelPayloadFieldNames.FIELD_NUM_MORAS_PREVIAS, request.getPreviousDefaultsCount());
        return modelPayload;
    }

}
