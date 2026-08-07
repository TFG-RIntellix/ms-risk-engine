package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;

/**
 * This enum represents the type of a request, which can be a loan, mortgage or
 * a credit card.
 * It contains the type of request as a string.
 * 
 * @author Lucía Fernández Mancebo
 * @date 25/04/2026
 */
public enum RequestType {
    PRESTAMO("PRESTAMO"),
    HIPOTECA("HIPOTECA"),
    TARJETA_CREDITO("TARJETA_CREDITO");

    private final String value;

    RequestType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Parses a string value to the corresponding RequestType enum.
     * Handles both JSON format ("TARJETA_CREDITO") and enum name format
     * (TARJETA_CREDITO).
     * 
     * @param value the string value to parse
     * @return the corresponding RequestType
     * @throws IllegalArgumentException if the value doesn't match any RequestType
     */
    public static RequestType fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException(LogMessage.REQUEST_TYPE_NULL);
        }

        for (RequestType type : RequestType.values()) {
            if (type.value.equals(value) || type.name().equals(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException(LogMessage.UNKNOWN_REQUEST_TYPE + value);
    }
}
