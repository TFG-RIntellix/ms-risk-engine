package es.NTTEnterprise.RIntellix.ms_risk_engine.application.constraints;

import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidSimulationFormChangesValidator
        implements ConstraintValidator<ValidSimulationFormChanges, Map<String, Object>> {

    private static final Set<String> ALLOWED_KEYS = Set.of(
            "interestRate",
            "termMonths",
            "requestedAmount",
            "annualIncome",
            "employmentStatus",
            "hasMortgage");

    @Override
    public boolean isValid(final Map<String, Object> value, final ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return value.keySet().stream().allMatch(ALLOWED_KEYS::contains);
    }
}
