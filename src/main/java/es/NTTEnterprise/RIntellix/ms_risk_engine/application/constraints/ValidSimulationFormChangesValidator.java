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
            "interestRate", "interest_rate",      // Scenario: different interest rates
            "termMonths", "term_months",          // Scenario: different loan terms
            "requestedAmount", "loanAmount", "amount", // Scenario: different loan amounts
            "requestedCreditLimit", "requested_credit_limit", "creditLimit", "credit_limit",
            "annualIncome", "annual_income",      // Scenario: different income levels
            "employmentStatus", "employment_status", // Scenario: employment status changes
            "hasMortgage", "has_mortgage",        // Scenario: mortgage obligation changes
            "isRevolving", "is_revolving",        // Scenario: credit card type changes
            "revolvingMinimumPayment", "revolving_minimum_payment",
            "revolvingPaymentType", "revolving_payment_type",
            "nrDependants", "nr_dependants", "dependents", "dependants");

    @Override
    public boolean isValid(final Map<String, Object> value, final ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return value.keySet().stream().allMatch(ALLOWED_KEYS::contains);
    }
}
