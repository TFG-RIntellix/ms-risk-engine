package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

/**
 * Centralized constants for model input field names used in simulation
 * calculations.
 * These field names correspond to the model field names expected by the AI
 * model.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-18-2026
 */
public final class SimulationFieldNames {

    private SimulationFieldNames() {
        throw new UnsupportedOperationException(LogMessage.UTILITY_CLASS_NEVER_INSTANTIATE);
    }

    // ============================================================
    // LOAN/MORTGAGE MODEL INPUT FIELDS (model naming convention)
    // ============================================================

    /** Requested loan amount field name in model input. */
    public static final String MODEL_FIELD_REQUESTED_AMOUNT = "loanAmount";

    /** Interest rate field name in model input. */
    public static final String MODEL_FIELD_INTEREST_RATE = "interestRate";

    /** Loan term in months field name in model input. */
    public static final String MODEL_FIELD_TERM_MONTHS = "termMonths";

    /** Annual income field name in model input. */
    public static final String MODEL_FIELD_ANNUAL_INCOME = "annualIncome";

    /** Debt-to-Income ratio field name in model input. */
    public static final String MODEL_FIELD_DTI = "dti";

    /** Loan-to-Value ratio field name in model input. */
    public static final String MODEL_FIELD_LTV = "ltv";

    // ============================================================
    // CREDIT CARD MODEL INPUT FIELDS (model naming convention)
    // ============================================================

    /** Credit limit field name in model input. */
    public static final String MODEL_FIELD_CREDIT_LIMIT = "creditLimit";

    /** Is revolving flag field name in model input. */
    public static final String MODEL_FIELD_IS_REVOLVING = "isRevolving";

    // ============================================================
    // FORM INPUT FIELDS (for simulation form changes)
    // ============================================================

    /**
     * Property value field name for mortgage simulations (NOT sent to model, used
     * for LTV calculation).
     */
    public static final String FORM_FIELD_PROPERTY_VALUE = "propertyValue";
}
