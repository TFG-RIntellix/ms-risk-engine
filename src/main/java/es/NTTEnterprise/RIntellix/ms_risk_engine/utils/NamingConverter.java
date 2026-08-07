package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;




/**
 * Core component: NamingConverter.
 * Encapsulates the logic and responsibilities assigned to this element
 * within the Hexagonal Architecture, ensuring separation of concerns.
 *
 * @author Lucía Fernández Mancebo
 * @date 28/07/2026
 */
public class NamingConverter {

    public String toCamelCase(final String rawFieldName) {
        if (rawFieldName == null || rawFieldName.isBlank()) {
            return rawFieldName;
        }

        final String trimmed = rawFieldName.trim();
        if (trimmed.contains("_")) {
            final String[] parts = trimmed.toLowerCase().split("[_]+");
            final StringBuilder camelCase = new StringBuilder(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                if (!parts[i].isEmpty()) {
                    camelCase.append(Character.toUpperCase(parts[i].charAt(0)));
                    if (parts[i].length() > 1) {
                        camelCase.append(parts[i].substring(1));
                    }
                }
            }
            return camelCase.toString();
        }

        if (trimmed.equals(trimmed.toUpperCase())) {
            return trimmed.toLowerCase();
        }

        return Character.toLowerCase(trimmed.charAt(0)) + trimmed.substring(1);
    }
}
