package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output.util;

import java.util.Map;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;

/**
 * Utility class for model payload operations.
 * 
 * Handles extraction and manipulation of payload data specifically for
 * model prediction requests. Centralizes payload transformation logic
 * for better testability and reusability.
 * 
 * @author Lucía Fernández Mancebo
 * @Date 05-08-2026
 */
public final class ModelPayloadUtil {

    private ModelPayloadUtil() {
        throw new UnsupportedOperationException(LogMessage.UTILITY_CLASS_NEVER_INSTANTIATE);
    }

    /**
     * Validates that the model payload is not null.
     * 
     * Used by adapters to ensure payload validity before processing.
     *
     * @param modelPayload the payload to validate.
     * @throws IllegalArgumentException if the payload is null.
     */
    public static void validatePayloadNotNull(final Map<String, Object> modelPayload) {
        if (modelPayload == null) {
            throw new IllegalArgumentException(LogMessage.PAYLOAD_NULL_ERROR);
        }
    }
}
