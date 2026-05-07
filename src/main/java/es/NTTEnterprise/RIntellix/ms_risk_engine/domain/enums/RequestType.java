package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This enum represents the type of a request, which can be a loan, mortgage or
 * a credit card.
 * It contains the type of request as a string.
 * 
 * @author Lucía Fernández Mancebo
 * @Date 04-25-2026
 */
public enum RequestType {
    PRESTAMO("PRESTAMO"),
    HIPOTECA("HIPOTECA"),
    TARJETA_CREDITO("TARJETA DE CREDITO");

    private final String value;

    RequestType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Parses a string value to the corresponding RequestType enum.
     * Handles both JSON format ("TARJETA DE CREDITO") and enum name format
     * (TARJETA_CREDITO).
     * 
     * @param value the string value to parse
     * @return the corresponding RequestType
     * @throws IllegalArgumentException if the value doesn't match any RequestType
     */
    public static RequestType fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("RequestType value cannot be null");
        }

        for (RequestType type : RequestType.values()) {
            if (type.value.equals(value) || type.name().equals(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown RequestType value: " + value);
    }
}
