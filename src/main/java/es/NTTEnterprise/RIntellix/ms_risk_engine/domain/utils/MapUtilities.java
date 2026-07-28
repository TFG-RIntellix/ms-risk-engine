package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.utils;

import java.util.Map;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.InvalidFormChangesException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;

/**
 * Utility class for safe operations on Map data structures.
 * 
 * Provides common methods for extracting typed values from maps with
 * null safety and default value fallbacks.
 *
 * @author Lucía Fernández Mancebo
 * @date 18/05/2026
 */
public final class MapUtilities {

    private MapUtilities() {
        throw new UnsupportedOperationException(LogMessage.UTILITY_CLASS_NEVER_INSTANTIATE);
    }

    /**
     * Safely extracts a double value from a map with a default fallback.
     * 
     * Handles null maps, missing keys, null values, and type conversions:
     * - Number types are converted via doubleValue()
     * - String types are parsed with Double.parseDouble()
     * - Other types return the default value
     *
     * @param source       the map to extract from.
     * @param key          the key to look up.
     * @param defaultValue the default value if key not found or value is null.
     * @return the double value or default value if extraction fails.
     * @throws InvalidFormChangesException if value is a String that cannot be
     *                                     parsed as double.
     */
    public static double getDouble(final Map<String, Object> source, final String key, final double defaultValue)
            throws InvalidFormChangesException {
        if (source == null || !source.containsKey(key) || source.get(key) == null) {
            return defaultValue;
        }
        final Object value = source.get(key);
        if (value instanceof Number numberValue) {
            return numberValue.doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException ex) {
            throw new InvalidFormChangesException(
                    String.format("Invalid numeric value for key '%s': '%s'", key, value), ex);
        }
    }

    /**
     * Safely extracts a boolean value from a map with a default fallback.
     * Handles Boolean types and "Si"/"true" string representations.
     *
     * @param source       the map to extract from.
     * @param key          the key to look up.
     * @param defaultValue the default value if key not found or value is null.
     * @return the boolean value.
     */
    public static boolean getBoolean(final Map<String, Object> source, final String key, final boolean defaultValue) {
        if (source == null || !source.containsKey(key) || source.get(key) == null) {
            return defaultValue;
        }
        final Object value = source.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            final String str = (String) value;
            return "true".equalsIgnoreCase(str) || "si".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str);
        }
        return defaultValue;
    }
}
