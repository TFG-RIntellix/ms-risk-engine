package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

/**
 * Centralized constants for simulation draft calculations.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
public final class SimulationConstants {

    private SimulationConstants() {
        throw new UnsupportedOperationException(LogMessage.UTILITY_CLASS_NEVER_INSTANTIATE);
    }

    public static final double MONTHS_PER_YEAR = 12.0;
    public static final double PERCENTAGE_DIVISOR = 100.0;
    public static final double ZERO_RATE = 0.0;
    public static final double ZERO_VALUE = 0.0;
    public static final int MIN_TERM_MONTHS = 1;
    public static final String RISK_GRADE_ARROW = " -> ";

    /**
     * Safely returns the value of a Double or ZERO_VALUE if null.
     *
     * @param value the Double value to check.
     * @return the double value or ZERO_VALUE if value is null.
     */
    public static double getSafe(final Double value) {
        return value == null ? ZERO_VALUE : value;
    }
}
