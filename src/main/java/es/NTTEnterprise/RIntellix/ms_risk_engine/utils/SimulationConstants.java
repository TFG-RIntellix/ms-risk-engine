package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

/**
 * Constants used across simulation draft calculations.
 *
 * Centralizes magic numbers and string tokens to keep
 * simulation logic consistent and maintainable.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-11-2026
 */
public final class SimulationConstants {

    private SimulationConstants() {
        throw new UnsupportedOperationException(LogMessage.UTILITY_CLASS_NEVER_INSTANTIATE);
    }

    public static final double ZERO_VALUE = 0.0;
    public static final int MIN_TERM_MONTHS = 1;
    public static final String RISK_GRADE_ARROW = "->";
    public static final String REVOLVING_PAYMENT_TYPE_PERCENTAGE = "PERCENTAGE";
    public static final String REVOLVING_PAYMENT_TYPE_FIXED = "FIXED_AMOUNT";
}
