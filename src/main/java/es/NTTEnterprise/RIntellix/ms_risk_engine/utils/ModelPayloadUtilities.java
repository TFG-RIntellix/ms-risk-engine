package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

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
 * @author Lucia Fernandez Mancebo
 * @Date 09-05-2026
 */
@Component
public class ModelPayloadUtilities {
    private final EnumNormalizer enumNormalizer;
    private final BooleanConverter booleanConverter;

    @Autowired
    public ModelPayloadUtilities(final EnumNormalizer enumNormalizer, final BooleanConverter booleanConverter) {
        this.enumNormalizer = enumNormalizer;
        this.booleanConverter = booleanConverter;
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

}
