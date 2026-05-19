package es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies;

import java.util.List;
import java.util.Objects;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;

/**
 * Resolves the correct model endpoint path based on request type.
 * 
 * This resolver is reusable across both scoring and simulation workflows,
 * eliminating code duplication and ensuring consistent model routing.
 * 
 * The endpoint path determines which AI model to invoke:
 * - Loan/Mortgage: /api/v1/risk/predict-loan
 * - Credit Card: /api/v1/risk/predict-credit-card
 * 
 * @author Lucía Fernández Mancebo
 */
public final class ModelEndpointResolver {

    private ModelEndpointResolver() {
        throw new UnsupportedOperationException(LogMessage.FACTORY_CLASS_NEVER_INSTANTIATE);
    }

    /**
     * Resolves the model endpoint path for a given request type.
     * 
     * Delegates to the appropriate strategy to determine the endpoint,
     * ensuring consistency between scoring and simulation.
     *
     * @param requestType the request type (PRESTAMO, HIPOTECA, TARJETA_CREDITO).
     * @param strategies  the available scoring strategies.
     * @return the model endpoint path (e.g., /api/v1/risk/predict-loan).
     * @throws IllegalArgumentException if request type is not supported.
     */
    public static String resolveEndpointPath(
            final String requestType,
            final List<ScoringModelExecutionStrategy> strategies) {
        Objects.requireNonNull(requestType, LogMessage.REQUEST_TYPE_CANNOT_BE_NULL);
        Objects.requireNonNull(strategies, LogMessage.STRATEGIES_LIST_CANNOT_BE_NULL);

        // Find the strategy that supports this request type
        for (ScoringModelExecutionStrategy strategy : strategies) {
            if (strategy.supports(requestType)) {
                // Use reflection to get the endpoint path from strategy
                // (strategies store it as a private field)
                return extractEndpointPath(strategy);
            }
        }

        throw new IllegalArgumentException(
                String.format(LogMessage.REQUEST_TYPE_NOT_FOUND, requestType));
    }

    /**
     * Extracts endpoint path from a strategy instance.
     * 
     * Strategies store their endpoint paths as private fields:
     * - LoanOrMortgageScoringModelExecutionStrategy: predictLoanPath
     * - CreditCardScoringModelExecutionStrategy: predictCreditCardPath
     */
    private static String extractEndpointPath(final ScoringModelExecutionStrategy strategy) {
        try {
            // Try loan/mortgage endpoint field first
            var field = strategy.getClass().getDeclaredField("predictLoanPath");
            field.setAccessible(true);
            return (String) field.get(strategy);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                // Try credit card endpoint field
                var field = strategy.getClass().getDeclaredField("predictCreditCardPath");
                field.setAccessible(true);
                return (String) field.get(strategy);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException("Cannot extract endpoint path from strategy", ex);
            }
        }
    }
}
