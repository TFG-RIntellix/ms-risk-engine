package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadUtilities;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.NamingConverter;

/**
 * Mapper for transforming merged simulation variables into model payload
 * format.
 *
 * Field names are normalized into camelCase. This keeps the mapper generic for
 * the common snake_case / camelCase / PascalCase cases without requiring a
 * separate alias registry.
 */
@Component
public class SimulationModelPayloadMapper {

    private static final Map<String, String> FIELD_ALIASES = Map.of(
            "workSector", ModelPayloadFieldNames.FIELD_OCCUPATION_SECTOR,
            "nrDependants", ModelPayloadFieldNames.FIELD_DEPENDENTS,
            "requestType", ModelPayloadFieldNames.FIELD_LOAN_TYPE);

    private final ModelPayloadUtilities payloadUtilities;
    private final NamingConverter namingConverter;

    public SimulationModelPayloadMapper(final ModelPayloadUtilities payloadUtilities,
            final NamingConverter namingConverter) {
        this.payloadUtilities = Objects.requireNonNull(payloadUtilities,
                LogMessage.MODEL_PAYLOAD_UTILITIES_CANNOT_BE_NULL);
        this.namingConverter = Objects.requireNonNull(namingConverter, LogMessage.NAMING_CONVERTER_CANNOT_BE_NULL);
    }

    public Map<String, Object> normalizeBaseVariables(final Map<String, Object> baseVariables) {
        Objects.requireNonNull(baseVariables, LogMessage.BASE_VARIABLES_CANNOT_BE_NULL);

        final Map<String, Object> normalized = new HashMap<>();
        for (final Map.Entry<String, Object> entry : baseVariables.entrySet()) {
            final String canonicalFieldName = resolveCanonicalFieldName(entry.getKey());
            final Object value = entry.getValue();

            normalized.put(canonicalFieldName, normalizeValue(canonicalFieldName, value));
        }

        return normalized;
    }

    public Map<String, Object> normalizeFormChangesToCamelcase(final Map<String, Object> formChanges) {
        if (formChanges == null || formChanges.isEmpty()) {
            return new HashMap<>();
        }

        final Map<String, Object> result = new HashMap<>();
        for (final Map.Entry<String, Object> entry : formChanges.entrySet()) {
            final String canonicalFieldName = resolveCanonicalFieldName(entry.getKey());
            result.put(canonicalFieldName, normalizeValue(canonicalFieldName, entry.getValue()));
        }

        return result;
    }

    private String resolveCanonicalFieldName(final String rawFieldName) {
        final String camelCaseFieldName = namingConverter.toCamelCase(rawFieldName);
        return FIELD_ALIASES.getOrDefault(camelCaseFieldName, camelCaseFieldName);
    }

    private Object normalizeValue(final String fieldName, final Object value) {
        if (value == null) {
            return null;
        }

        if (ModelPayloadFieldNames.FIELD_HAS_MORTGAGE.equals(fieldName)) {
            return payloadUtilities.toModelBoolean((Boolean) value);
        }

        if (ModelPayloadFieldNames.FIELD_INTEREST_RATE.equals(fieldName) && value instanceof Number numValue) {
            return payloadUtilities.normalizeInterestRateToFraction(numValue.doubleValue());
        }

        if (ModelPayloadFieldNames.FIELD_IS_REVOLVING.equals(fieldName) && value instanceof Boolean boolValue) {
            return payloadUtilities.toModelBoolean(boolValue);
        }

        if (value instanceof String stringValue && isEnumField(fieldName)) {
            return payloadUtilities.normalizeEnumForField(fieldName, stringValue);
        }

        return value;
    }

    // Feature name normalization delegated to NamingConverter

    private boolean isEnumField(final String fieldName) {
        return "gender".equals(fieldName)
                || "maritalStatus".equals(fieldName)
                || "education".equals(fieldName)
                || "employmentStatus".equals(fieldName)
                || "occupationSector".equals(fieldName)
                || "homeOwnership".equals(fieldName)
                || "loanType".equals(fieldName)
                || "purpose".equals(fieldName)
                || "incomeType".equals(fieldName);
    }
}
