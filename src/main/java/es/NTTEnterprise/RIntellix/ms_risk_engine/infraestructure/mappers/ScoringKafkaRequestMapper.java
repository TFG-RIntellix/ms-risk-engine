package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.mappers;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Mapper utility for inbound Kafka scoring payload conversion.
 *
 * @author Lucia Fernandez Mancebo
 * @date 2026-04-20
 */
public final class ScoringKafkaRequestMapper {

    private static final String REQUEST_TYPE = "requestType";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ScoringKafkaRequestMapper() {
        // Utility class
    }

    /**
     * Converts payload into target DTO type.
     *
     * @param payload    raw kafka payload
     * @param targetType target DTO class
     * @param <T>        target type
     * @return typed DTO instance
     */
    public static <T> T toType(final Object payload, final Class<T> targetType) {
        Objects.requireNonNull(targetType, "targetType must not be null");
        if (payload == null) {
            throw new IllegalArgumentException("Kafka payload must not be null");
        }

        if (targetType.isInstance(payload)) {
            return targetType.cast(payload);
        }

        return OBJECT_MAPPER.convertValue(payload, targetType);
    }

    /**
     * Extracts requestType from a generic payload.
     *
     * @param payload raw kafka payload
     * @return normalized request type or null when absent
     */
    public static String extractRequestType(final Object payload) {
        if (payload == null) {
            return null;
        }

        final Map<String, Object> payloadMap = OBJECT_MAPPER.convertValue(
                payload,
                new TypeReference<Map<String, Object>>() {
                });
        final Object requestTypeValue = payloadMap.get(REQUEST_TYPE);
        if (requestTypeValue == null) {
            return null;
        }

        return String.valueOf(requestTypeValue).trim().toUpperCase(Locale.ROOT);
    }
}
