package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationRequest;

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

    /**
     * Maps loan or mortgage generation request to model payload.
     *
     * @param request the source scoring generation request.
     * @return the model payload with Spanish field names.
     */
    public Map<String, Object> toModelPayload(final ScoringGenerationRequest request) {
        final Map<String, Object> modelPayload = new LinkedHashMap<>();
        modelPayload.put("edad", request.getAge());
        // TODO: Enum normalization applied below — ms-core-data should send values
        // matching ms-model enums directly.
        modelPayload.put("genero", normalizeEnum(request.getGender()));
        modelPayload.put("estado_civil", normalizeEnum(request.getMaritalStatus()));
        // Educacion enums use spaces: "Sin Estudios", "Formacion Profesional"
        modelPayload.put("educacion", normalizeEnumWithSpaces(request.getEducation()));
        modelPayload.put("situacion_laboral", normalizeEnum(request.getEmploymentStatus()));
        // Sector trabajo enums use spaces: "Sector Publico"
        modelPayload.put("sector_trabajo", normalizeEnumWithSpaces(request.getOccupationSector()));
        modelPayload.put("dependientes", request.getDependents());
        modelPayload.put("vivienda", normalizeEnum(request.getHomeOwnership()));
        modelPayload.put("tiene_hipoteca", Boolean.TRUE.equals(request.getHasMortgage()) ? YES_VALUE : NO_VALUE);
        modelPayload.put("ingresos_anuales", request.getAnnualIncome());
        // TODO: tipo_prestamo defaults to "Personal" — ms-core-data should provide the
        // specific loan type enum value.
        modelPayload.put("tipo_prestamo", request.getLoanType());
        modelPayload.put("proposito", normalizeEnum(request.getPurpose()));
        modelPayload.put("monto_prestamo", request.getLoanAmount());
        modelPayload.put("plazo_meses", request.getTermMonths());
        modelPayload.put("tasa_interes", request.getInterestRate());
        // TODO: null LTV defaulted to 0.0 — ms-core-data should compute and provide
        // this value.
        modelPayload.put("ltv", request.getLtv() != null ? request.getLtv() : DEFAULT_LTV);
        modelPayload.put("dti", request.getDti());
        modelPayload.put("num_prestamos_previos", request.getPreviousLoansCount());
        modelPayload.put("num_moras_previas", request.getPreviousDefaultsCount());
        return modelPayload;
    }

    /**
     * Normalizes UPPERCASE_VALUE to Title_Case (underscore-separated).
     * Used for enums like ViviendaEnum ("Propia_Hipoteca") and PropositoEnum
     * ("Compra_Vehiculo").
     *
     * TODO: Workaround — ms-core-data should send values in model-expected format.
     *
     * @param value the raw enum value.
     * @return the normalized value, or null if input is null.
     */
    private String normalizeEnum(final String value) {
        return normalizeValue(value, "_");
    }

    /**
     * Normalizes UPPERCASE_VALUE to Title Case (space-separated).
     * Used for enums like EducacionEnum ("Sin Estudios", "Formacion Profesional")
     * and SectorTrabajoEnum ("Sector Publico").
     *
     * TODO: Workaround — ms-core-data should send values in model-expected format.
     *
     * @param value the raw enum value.
     * @return the normalized value, or null if input is null.
     */
    private String normalizeEnumWithSpaces(final String value) {
        return normalizeValue(value, " ");
    }

    private String normalizeValue(final String value, final String separator) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        final String[] parts = value.split("[_ ]");
        final StringBuilder normalized = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                normalized.append(separator);
            }
            if (!parts[i].isEmpty()) {
                normalized.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    normalized.append(parts[i].substring(1).toLowerCase());
                }
            }
        }
        return normalized.toString();
    }
}
