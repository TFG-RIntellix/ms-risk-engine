package es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies;

import java.util.List;
import java.util.Objects;

/**
 * Factory class for creating ScoringModelExecutionStrategy instances based on
 * request type.
 * 
 */
public final class ScoringModelExecutionStrategyFactory {

    private ScoringModelExecutionStrategyFactory() {
        throw new UnsupportedOperationException("Never instantiate");
    }

    /**
     * Creates the appropriate scoring execution strategy based on request type.
     *
     * Uses existing Spring-injected strategy beans instead of creating new
     * strategy instances manually.
     *
     * @param requestType the request type; must not be null.
     * @param strategies  the available strategy beans; must not be null.
     * @return a ScoringModelExecutionStrategy suitable for the request type.
     * @throws IllegalArgumentException if request type is not recognized.
     * @throws NullPointerException     if request type or strategies is null.
     */
    public static ScoringModelExecutionStrategy createStrategy(
            final String requestType,
            final List<ScoringModelExecutionStrategy> strategies) {
        Objects.requireNonNull(requestType, "Request type cannot be null");
        Objects.requireNonNull(strategies, "strategies cannot be null");

        for (ScoringModelExecutionStrategy strategy : strategies) {
            if (strategy.supports(requestType)) {
                return strategy;
            }
        }

        throw new IllegalArgumentException("Unsupported request type: " + requestType);
    }

}