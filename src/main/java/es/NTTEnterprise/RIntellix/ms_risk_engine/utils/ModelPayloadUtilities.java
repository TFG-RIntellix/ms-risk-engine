package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

import org.springframework.stereotype.Component;

/**
 * Utility component for handling model payload transformations.
 * Consolidates enum normalization and field name translation logic used by
 * mappers.
 *
 * Responsibilities:
 * - Normalize enum values from UPPERCASE_WITH_UNDERSCORE to Title_Case or Title
 * Case
 * - Translate English field names to Spanish model field names
 * - Provide common transformation utilities for all payload mappers
 *
 * @author Lucia Fernandez Mancebo
 * @Date 09-05-2026
 */
@Component
public class ModelPayloadUtilities {

    /**
     * Normalizes UPPERCASE_VALUE to Title_Case (underscore-separated).
     * Used for enums like ViviendaEnum ("Propia_Hipoteca") and PropositoEnum
     * ("Compra_Vehiculo").
     *
     * Example: "PROPIA_HIPOTECA" → "Propia_Hipoteca"
     *
     * @param value the raw enum value.
     * @return the normalized value, or null if input is null or empty.
     */
    public String normalizeEnumToTitleCase(final String value) {
        return normalizeValue(value, "_");
    }

    /**
     * Normalizes UPPERCASE_VALUE to Title Case (space-separated).
     * Used for enums like EducacionEnum ("Sin Estudios", "Formacion Profesional")
     * and SectorTrabajoEnum ("Sector Publico").
     *
     * Example: "FORMACION_PROFESIONAL" → "Formacion Profesional"
     *
     * @param value the raw enum value.
     * @return the normalized value, or null if input is null or empty.
     */
    public String normalizeEnumToTitleCaseWithSpaces(final String value) {
        return normalizeValue(value, " ");
    }

    /**
     * Internal method that performs the actual normalization.
     * Splits by underscore or space, capitalizes first letter of each word,
     * lowercases the rest, and rejoins with the specified separator.
     *
     * @param value     the raw enum value.
     * @param separator the separator to use when rejoining ("_" or " ").
     * @return the normalized value, or null if input is null or empty.
     */
    private String normalizeValue(final String value, final String separator) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // Split by both underscore and space to handle mixed separators
        final String[] parts = value.split("[_ ]");
        final StringBuilder normalized = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                normalized.append(separator);
            }
            if (!parts[i].isEmpty()) {
                normalized.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    normalized.append(parts[i].substring(1).toLowerCase());
                }
            }
        }
        return normalized.toString();
    }

    /**
     * Translates English field names to Spanish model field names.
     * Used when mapping application field names to ms-model API expectations.
     *
     * Maps common loan/mortgage and credit card fields to their Spanish
     * equivalents.
     *
     * @param englishFieldName the English field name (e.g., "age", "gender").
     * @return the corresponding Spanish field name (e.g., "edad", "genero"),
     *         or the original field name if no translation exists.
     */
    // TODO: Load this from configuration considering by using yaml or
    // application.properties fields.
    public String translateToSpanishFieldName(final String englishFieldName) {
        if (englishFieldName == null || englishFieldName.isEmpty()) {
            return englishFieldName;
        }

        return switch (englishFieldName) {
            // Demographics
            case "age" -> "edad";
            case "gender" -> "genero";
            case "maritalStatus" -> "estado_civil";
            case "education" -> "educacion";
            case "employmentStatus" -> "situacion_laboral";
            case "occupationSector" -> "sector_trabajo";
            case "dependents" -> "dependientes";

            // Housing
            case "homeOwnership" -> "vivienda";
            case "hasMortgage" -> "tiene_hipoteca";

            // Income
            case "annualIncome" -> "ingresos_anuales";

            // Loan parameters
            case "loanType" -> "tipo_prestamo";
            case "purpose" -> "proposito";
            case "loanAmount" -> "monto_prestamo";
            case "termMonths" -> "plazo_meses";
            case "interestRate" -> "tasa_interes";
            case "ltv" -> "ltv";
            case "dti" -> "dti";

            // Credit history
            case "previousLoansCount" -> "num_prestamos_previos";
            case "previousDefaultsCount" -> "num_moras_previas";

            // Credit card specific
            case "creditLimit" -> "limite_credito";
            case "isRevolving" -> "es_revolvente";
            case "utilizationRatio" -> "tasa_utilizacion";

            // Default: return unchanged if no translation exists
            default -> englishFieldName;
        };
    }
}
