package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;



/**
 * Small utility for normalizing enum-like values into human-friendly strings.
 */

/**
 * Core component: EnumNormalizer.
 * Encapsulates the logic and responsibilities assigned to this element
 * within the Hexagonal Architecture, ensuring separation of concerns.
 *
 * @author Lucía Fernández Mancebo
 * @date 28/07/2026
 */
public class EnumNormalizer {

    public String normalizeToTitleCase(final String value, final boolean withSpaces) {
        return normalizeValue(value, withSpaces ? " " : "_");
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
