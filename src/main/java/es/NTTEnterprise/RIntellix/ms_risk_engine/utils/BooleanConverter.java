package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;



/**
 * Converts boolean values to the model's expected string representation.
 */

public class BooleanConverter {

    public String toModelBoolean(final Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? ModelPayloadConstants.BOOLEAN_VALUE_YES : ModelPayloadConstants.BOOLEAN_VALUE_NO;
    }
}
