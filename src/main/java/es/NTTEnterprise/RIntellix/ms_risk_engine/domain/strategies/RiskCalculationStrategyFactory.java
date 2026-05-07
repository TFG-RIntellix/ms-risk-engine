package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies;

import java.util.List;
import java.util.Objects;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.RiskCalculationStrategy;

/**
 * Factory class for resolving the appropriate risk calculation strategy
 * based on request type and revolving flag.
 *
 * For credit card types, the isRevolving flag disambiguates between
 * standard and revolving risk calculation strategies.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-25-2026
 */
public final class RiskCalculationStrategyFactory {

    private RiskCalculationStrategyFactory() {
        throw new UnsupportedOperationException("Never instantiate");
    }

    /**
     * Resolves the appropriate risk calculation strategy.
     *
     * @param requestType the normalized request type; must not be null.
     * @param isRevolving the revolving flag for credit card disambiguation;
     *                    null for non-credit-card types.
     * @param strategies  the available strategy beans; must not be null.
     * @return the matching RiskCalculationStrategy.
     * @throws IllegalArgumentException if no strategy supports the given combination.
     * @throws NullPointerException     if requestType or strategies is null.
     */
    public static RiskCalculationStrategy createStrategy(
            final String requestType,
            final Boolean isRevolving,
            final List<RiskCalculationStrategy> strategies) {
        Objects.requireNonNull(requestType, "Request type cannot be null");
        Objects.requireNonNull(strategies, "Strategies list cannot be null");

        for (RiskCalculationStrategy strategy : strategies) {
            if (strategy.supports(requestType, isRevolving)) {
                return strategy;
            }
        }

        throw new IllegalArgumentException(
                "No risk calculation strategy found for requestType=" + requestType
                        + ", isRevolving=" + isRevolving);
    }
}
