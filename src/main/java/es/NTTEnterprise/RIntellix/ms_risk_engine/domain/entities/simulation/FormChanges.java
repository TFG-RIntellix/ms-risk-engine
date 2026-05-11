package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain entity representing the set of form changes applied by the user
 * during a simulation scenario.
 *
 * Wraps a map of modified fields to preserve flexibility across product types.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-11-2026
 */
public class FormChanges {

    private Map<String, Object> values;

    /**
     * Default constructor initializing an empty form changes map.
     */
    public FormChanges() {
        this.values = new HashMap<>();
    }

    /**
     * Constructs a FormChanges instance with the provided values.
     *
     * @param values the map of modified fields.
     */
    public FormChanges(final Map<String, Object> values) {
        this.values = values == null ? new HashMap<>() : new HashMap<>(values);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(final Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final FormChanges other = (FormChanges) obj;
        return Objects.equals(values, other.values);
    }

    @Override
    public String toString() {
        return "FormChanges{values=" + values + '}';
    }
}
