package es.NTTEnterprise.RIntellix.ms_risk_engine.application.constraints;

import java.util.Set;

/**
 * Centralized constants for allowed simulation form field names.
 * 
 * Defines which field names can be modified in simulation what-if scenarios.
 * Used by ValidSimulationFormChangesValidator to ensure only valid fields
 * are accepted in form changes.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-18-2026
 */
public final class SimulationFormFieldNames {

    private SimulationFormFieldNames() {
        // Private constructor to prevent instantiation
    }

    // ============================================================
    // ALLOWED FORM FIELD NAMES FOR SIMULATION
    // ============================================================

    /** Allowed simulation form fields that can be modified in what-if scenarios */
    public static final Set<String> ALLOWED_FIELD_NAMES = Set.of(
            "interestRate", // Scenario: different interest rates
            "termMonths", // Scenario: different loan terms
            "loanAmount", // Scenario: different loan amounts
            "annualIncome", // Scenario: different income levels
            "employmentStatus", // Scenario: employment status changes
            "hasMortgage", // Scenario: mortgage obligation changes
            "propertyValue", // Mortgage: property value to calculate LTV (for mortgage only)
            "creditLimit"); // Credit card: credit limit field
}
