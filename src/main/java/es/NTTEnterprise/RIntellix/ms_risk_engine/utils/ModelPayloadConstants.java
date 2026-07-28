package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

/**
 * Centralized constants for model payload transformations.
 * 
 * Contains hardcoded values used when mapping domain objects to model payloads,
 * including boolean representations expected by the model, default values, and
 * other constants specific to the AI model requirements.
 *
 * @author Lucía Fernández Mancebo
 * @date 18/05/2026
 */
public final class ModelPayloadConstants {

    private ModelPayloadConstants() {
        throw new UnsupportedOperationException(LogMessage.UTILITY_CLASS_NEVER_INSTANTIATE);
    }

    // ============================================================
    // BOOLEAN REPRESENTATIONS IN MODEL PAYLOAD (model-expected)
    // ============================================================

    /** Boolean TRUE representation in model payload as expected by the model. */
    public static final String BOOLEAN_VALUE_YES = "Si";

    /** Boolean FALSE representation in model payload as expected by the model. */
    public static final String BOOLEAN_VALUE_NO = "No";

    // ============================================================
    // DEFAULT VALUES FOR MODEL PAYLOAD
    // ============================================================

    /**
     * Default LTV value applied when ms-core-data sends null.
     */
    public static final double DEFAULT_LTV = 0.0;
}
