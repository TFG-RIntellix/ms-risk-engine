package es.NTTEnterprise.RIntellix.ms_risk_engine.application.constraints;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Map;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Core component: ValidSimulationFormChangesValidator.
 * Encapsulates the logic and responsibilities assigned to this element
 * within the Hexagonal Architecture, ensuring separation of concerns.
 *
 * @author Lucía Fernández Mancebo
 * @date 28/07/2026
 */
public class ValidSimulationFormChangesValidator
        implements ConstraintValidator<ValidSimulationFormChanges, Map<String, Object>> {

    @Override
    public boolean isValid(final Map<String, Object> value, final ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        List<String> unsupportedFields = value.keySet().stream()
                .filter(key -> !SimulationFormFieldNames.ALLOWED_FIELD_NAMES.contains(key))
                .collect(Collectors.toList());

        if (unsupportedFields.isEmpty()) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate() + ": " + unsupportedFields)
                .addConstraintViolation();

        return false;
    }
}
