package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

import java.util.Objects;



import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;

/**
 * Utility component for handling model payload transformations.
 * Consolidates enum normalization and field name translation logic used by
 * mappers.
 *
 * Responsibilities:
 * - Normalize enum values from UPPERCASE_WITH_UNDERSCORE to Title_Case or Title
 * Case
 * - Translate English field names to the canonical model field names
 * - Provide common transformation utilities for all payload mappers
 *
 * @author Lucía Fernández Mancebo
 * @date 05/09/2026
 */

public class ModelPayloadUtilities {
    private final EnumNormalizer enumNormalizer;
    private final BooleanConverter booleanConverter;

    public ModelPayloadUtilities(final EnumNormalizer enumNormalizer, final BooleanConverter booleanConverter) {
        this.enumNormalizer = Objects.requireNonNull(enumNormalizer, LogMessage.ENUM_NORMALIZER_CANNOT_BE_NULL);
        this.booleanConverter = Objects.requireNonNull(booleanConverter, LogMessage.BOOLEAN_CONVERTER_CANNOT_BE_NULL);
    }

    public String normalizeEnumToField(final String value, final boolean withSpaces) {
        return enumNormalizer.normalizeToTitleCase(value, withSpaces);
    }

    public String toModelBoolean(final Boolean value) {
        return booleanConverter.toModelBoolean(value);
    }

    /**
     * Normalize enum-like values for a given field by auto-detecting whether
     * the input uses spaces. If the input contains spaces the normalized value
     * will be produced using spaces; otherwise underscores will be preserved.
     */
    public String normalizeEnumForField(final String fieldName, final String value) {
        if (value == null) {
            return null;
        }
        final boolean withSpaces = value.contains(" ");
        return enumNormalizer.normalizeToTitleCase(value, withSpaces);
    }

    /**
     * Normalizes the interest rate from a percentage format (e.g., 24.91) to a decimal fraction format (e.g., 0.2491).
     *
     * @param interestRate the interest rate in percentage format.
     * @return the interest rate as a fraction, or null if input is null.
     */
    public Double normalizeInterestRateToFraction(final Double interestRate) {
        if (interestRate == null) {
            return null;
        }
        return interestRate / SimulationConstants.PERCENTAGE_DIVISOR;
    }

}
