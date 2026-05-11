package es.NTTEnterprise.RIntellix.ms_risk_engine.application.constraints;

import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;

public class ValidSimulationFormChangesValidator
        implements ConstraintValidator<ValidSimulationFormChanges, Map<String, Object>> {

    // Allowed simulation form fields that can be modified in what-if scenarios
    private static final Set<String> ALLOWED_KEYS = Set.of(
            "interestRate",      // Scenario: different interest rates
            "termMonths",        // Scenario: different loan terms
            "requestedAmount",   // Scenario: different loan amounts
            "annualIncome",      // Scenario: different income levels
            "employmentStatus",  // Scenario: employment status changes
            "hasMortgage");      // Scenario: mortgage obligation changes

    @Override
    public boolean isValid(final Map<String, Object> value, final ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return value.keySet().stream().allMatch(ALLOWED_KEYS::contains);
    }
}
