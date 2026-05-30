package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

import org.springframework.stereotype.Component;

@Component
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
