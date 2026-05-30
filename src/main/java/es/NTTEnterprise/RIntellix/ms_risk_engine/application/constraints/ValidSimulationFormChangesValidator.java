package es.NTTEnterprise.RIntellix.ms_risk_engine.application.constraints;

import java.util.Map;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidSimulationFormChangesValidator
        implements ConstraintValidator<ValidSimulationFormChanges, Map<String, Object>> {

    @Override
    public boolean isValid(final Map<String, Object> value, final ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return value.keySet().stream().allMatch(SimulationFormFieldNames.ALLOWED_FIELD_NAMES::contains);
    }
}
