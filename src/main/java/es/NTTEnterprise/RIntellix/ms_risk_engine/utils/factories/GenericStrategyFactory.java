package es.NTTEnterprise.RIntellix.ms_risk_engine.utils.factories;

import java.util.List;

import jakarta.validation.constraints.NotNull;

/**
 * Generic factory for selecting a strategy from a list based on a matching
 * criterion.
 * This factory abstracts the common logic of iterating through a list of
 * strategies
 * and selecting the one that supports a given request type or other criteria.
 * The specific matching logic is provided by the caller through a
 * StrategyMatcher functional interface.
 * This factory is designed to be reusable across different types of strategies,
 * such as
 * RiskCalculationStrategy and ScoringModelExecutionStrategy, to avoid code
 * duplication in the specific factories.
 * 
 * @author Lucía Fernández Mancebo
 * @date 25/04/2026
 */
public class GenericStrategyFactory {

    public interface StrategyMatcher<T> {
        boolean supports(T strategy);
    }

    /**
     * Selects a strategy from the provided list based on the given matcher.
     * 
     * @param <T>          the type of strategy
     * @param strategies   the list of available strategies; must not be null.
     * @param matcher      the functional interface that defines the matching logic;
     *                     must not be null.
     * @param errorMessage the error message to use if no matching strategy is
     *                     found; must not be null.
     * @return the first strategy that matches the criteria defined by the matcher.
     * @throws IllegalArgumentException if no matching strategy is found.
     * @throws NullPointerException     if strategies, matcher, or errorMessage is
     *                                  null.
     */
    public static <T> T selectStrategy(
            @NotNull List<T> strategies,
            @NotNull StrategyMatcher<T> matcher,
            @NotNull String errorMessage) {
        return strategies.stream()
                .filter(s -> matcher.supports(s))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(errorMessage));
    }
}
