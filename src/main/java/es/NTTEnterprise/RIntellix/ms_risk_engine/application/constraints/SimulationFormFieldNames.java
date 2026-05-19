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
            "Tasa_Interes", // Scenario: different interest rates
            "Plazo_Meses", // Scenario: different loan terms
            "Monto_Prestamo", // Scenario: different loan amounts
            "Ingresos_Anuales", // Scenario: different income levels
            "Situacion_Laboral", // Scenario: employment status changes
            "Tiene_Hipoteca", // Scenario: mortgage obligation changes
            "propertyValue"); // Mortgage: property value to calculate LTV (for HIPOTECA only)
}
