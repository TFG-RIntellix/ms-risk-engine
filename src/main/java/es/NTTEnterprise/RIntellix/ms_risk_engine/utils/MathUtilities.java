package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for mathematical operations, such as rounding.
 * 
 * @author Lucia Fernandez Mancebo
 * @Date 05-30-2026
 */
public final class MathUtilities {

    private MathUtilities() {
        throw new UnsupportedOperationException(LogMessage.UTILITY_CLASS_NEVER_INSTANTIATE);
    }

    /**
     * Rounds a value to 2 decimal places.
     * Use this for final values that are retrieved or displayed by the API.
     * 
     * @param value the value to round
     * @return the rounded value
     */
    public static double roundFinal(final double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return value;
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Rounds a value to 4 decimal places.
     * Use this for intermediate values used in calculations that are not displayed.
     * 
     * @param value the value to round
     * @return the rounded value
     */
    public static double roundIntermediate(final double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return value;
        }
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }
}
