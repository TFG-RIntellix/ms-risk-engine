package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation;

import java.util.HashMap;
import java.util.Map;

/**
 * Value object representing user-edited simulation form values.
 *
 * @author Lucía Fernández Mancebo
 * @date 10/05/2026
 */
public class FormChanges {

    private final Map<String, Object> values;

    public FormChanges(final Map<String, Object> values) {
        this.values = values == null ? Map.of() : Map.copyOf(values);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public Map<String, Object> toMutableMap() {
        return new HashMap<>(values);
    }
}
