package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



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

/**
 * Core component: SimulationModelPayloadMapper.
 * Encapsulates the logic and responsibilities assigned to this element
 * within the Hexagonal Architecture, ensuring separation of concerns.
 *
 * @author Lucía Fernández Mancebo
 * @date 28/07/2026
 */
public class SimulationModelPayloadMapper {

    private final ModelPayloadUtilities payloadUtilities;
    private final NamingConverter namingConverter;

    public SimulationModelPayloadMapper(final ModelPayloadUtilities payloadUtilities,
            final NamingConverter namingConverter) {
        this.payloadUtilities = Objects.requireNonNull(payloadUtilities,
                LogMessage.MODEL_PAYLOAD_UTILITIES_CANNOT_BE_NULL);
        this.namingConverter = Objects.requireNonNull(namingConverter, LogMessage.NAMING_CONVERTER_CANNOT_BE_NULL);
    }

    public Map<String, Object> normalizeBaseVariables(final Map<String, Object> baseVariables, final String requestType) {
        Objects.requireNonNull(baseVariables, LogMessage.BASE_VARIABLES_CANNOT_BE_NULL);

        final Map<String, Object> normalized = new HashMap<>();
        for (final Map.Entry<String, Object> entry : baseVariables.entrySet()) {
            final String canonicalFieldName = resolveCanonicalFieldName(entry.getKey(), requestType);
            final Object value = entry.getValue();

            normalized.put(canonicalFieldName, normalizeValue(canonicalFieldName, value));
        }

        return normalized;
    }

    public Map<String, Object> normalizeFormChangesToCamelcase(final Map<String, Object> formChanges, final String requestType) {
        if (formChanges == null || formChanges.isEmpty()) {
            return new HashMap<>();
        }

        final Map<String, Object> result = new HashMap<>();
        for (final Map.Entry<String, Object> entry : formChanges.entrySet()) {
            final String canonicalFieldName = resolveCanonicalFieldName(entry.getKey(), requestType);
            result.put(canonicalFieldName, normalizeValue(canonicalFieldName, entry.getValue()));
        }

        return result;
    }

    private String resolveCanonicalFieldName(final String rawFieldName, final String requestType) {
        final String camelCaseFieldName = namingConverter.toCamelCase(rawFieldName);
        final String alias = ModelPayloadFieldNames.FIELD_ALIASES.getOrDefault(camelCaseFieldName, camelCaseFieldName);
        
        // Intelligent mapping: convert loanAmount/requestedAmount to creditLimit for credit cards
        if ("TARJETA_CREDITO".equalsIgnoreCase(requestType) && ModelPayloadFieldNames.FIELD_LOAN_AMOUNT.equals(alias)) {
            return ModelPayloadFieldNames.FIELD_CREDIT_LIMIT;
        }
        
        return alias;
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
        if (value instanceof Number numValue) {
            return numValue.doubleValue();
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
