package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import java.util.Map;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RequestType;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.FinancialMetricsCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * Domain service for calculating critical risk indicators (DTI and LTV).
 * 
 * Encapsulates business logic for recalculating risk metrics during simulation,
 * ensuring that critical input features are properly computed before model
 * invocation.
 * 
 * This service follows the Hexagonal Architecture pattern by being a domain
 * service
 * that contains pure business logic (no dependencies on application or
 * infrastructure layers).
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-18-2026
 */
@Slf4j
public class RiskIndicatorCalculationService {

    /**
     * Recalculates critical risk indicators (DTI and LTV) before model invocation.
     * 
     * Process:
     * 1. Extract simulated loan parameters from merged variables
     * 2. Calculate new monthly payment based on simulated amount, rate, and term
     * 3. Calculate new DTI = monthlyPayment / annualIncome
     * 4. Update DTI in merged variables (critical for model)
     * 5. If mortgage (HIPOTECA):
     * - Resolve property value (user override or base value)
     * - Calculate LTV = loanAmount / propertyValue
     * - Update LTV in merged variables
     * - Remove propertyValue from merged variables (NOT a model feature)
     *
     * @param mergedVariables   the merged simulation variables (updated in-place).
     * @param requestType       the request type (PRESTAMO, HIPOTECA,
     *                          TARJETA_CREDITO).
     * @param baseInputSnapshot the base input snapshot containing original
     *                          parameters.
     */
    public void recalculateRiskIndicators(
            final Map<String, Object> mergedVariables,
            final String requestType,
            final Map<String, Object> baseInputSnapshot) {

        try {

            // Extract new parameters needed.
            final double loanAmount = getDouble(mergedVariables, ModelPayloadFieldNames.FIELD_LOAN_AMOUNT, 0);
            final double interestRate = getDouble(mergedVariables, ModelPayloadFieldNames.FIELD_INTEREST_RATE, 0);
            final int termMonths = (int) getDouble(mergedVariables, ModelPayloadFieldNames.FIELD_TERM_MONTHS,
                    SimulationConstants.MIN_TERM_MONTHS);
            final double annualIncome = getDouble(mergedVariables, ModelPayloadFieldNames.FIELD_ANNUAL_INCOME, 0);

            // Step 1: Recalculate DTI based on new parameters
            final double monthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(
                    loanAmount, interestRate, termMonths);

            final double newDti = FinancialMetricsCalculator.calculateDti(monthlyPayment, annualIncome);

            // Update DTI in merged variables
            mergedVariables.put(ModelPayloadFieldNames.FIELD_DTI, newDti);
            log.info(LogMessage.SIMULATION_DTI_RECALCULATED, newDti, monthlyPayment, annualIncome);

            // Step 2: Handle LTV for mortgages (HIPOTECA)
            if (isMortgage(requestType)) {
                recalculateLtvForMortgage(mergedVariables, baseInputSnapshot, loanAmount);
            }

        } catch (IllegalArgumentException | ClassCastException | NullPointerException e) {
            log.warn(LogMessage.SIMULATION_RISK_INDICATORS_CALCULATION_ERROR, e.getMessage());
        }
    }

    /**
     * Recalculates LTV (Loan-to-Value) for mortgage requests.
     * Process:
     * 1. Resolve property value (user-provided or from base snapshot)
     * 2. Calculate LTV = loanAmount / propertyValue
     * 3. Update LTV in merged variables
     * 4. Remove propertyValue from merged variables (not a model feature)
     *
     * @param mergedVariables   the merged variables (updated in-place).
     * @param baseInputSnapshot the base input snapshot containing original
     *                          parameters.
     * @param loanAmount        the simulated loan amount.
     */
    private void recalculateLtvForMortgage(
            final Map<String, Object> mergedVariables,
            final Map<String, Object> baseInputSnapshot,
            final double loanAmount) {

        // Determine property value to use for LTV calculation:
        // Priority 1: User-provided propertyValue in form changes (via mergedVariables)
        // Priority 2: Property value from base input snapshot
        Double propertyValue = (Double) mergedVariables.get(ModelPayloadFieldNames.FIELD_PROPERTY_VALUE);
        if (propertyValue == null && baseInputSnapshot != null) {
            propertyValue = (Double) baseInputSnapshot.get(ModelPayloadFieldNames.FIELD_PROPERTY_VALUE);
        }

        // If we have a valid property value, calculate and set LTV
        if (propertyValue != null && propertyValue > 0) {
            final double newLtv = loanAmount / propertyValue;
            mergedVariables.put(ModelPayloadFieldNames.FIELD_LTV, newLtv);
            log.debug(LogMessage.SIMULATION_LTV_RECALCULATED, newLtv, loanAmount, propertyValue);
        }

        // CRITICAL: Remove propertyValue from model input
        // propertyValue is NOT a model feature, only LTV is
        mergedVariables.remove(ModelPayloadFieldNames.FIELD_PROPERTY_VALUE);
        log.debug(LogMessage.SIMULATION_PROPERTY_VALUE_REMOVED);
    }

    /**
     * Checks if the request type is a mortgage (HIPOTECA).
     *
     * @param requestType the request type string.
     * @return true if the request type represents a mortgage.
     */
    private boolean isMortgage(final String requestType) {
        if (requestType == null) {
            return false;
        }
        try {
            return RequestType.fromValue(requestType) == RequestType.HIPOTECA;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Safely extracts a double value from a map with a default fallback.
     *
     * @param map          the map to extract from.
     * @param key          the key to look up.
     * @param defaultValue the default value if key not found or value is null.
     * @return the double value or default.
     */
    private double getDouble(final Map<String, Object> map, final String key, final double defaultValue) {
        if (map == null || !map.containsKey(key)) {
            return defaultValue;
        }
        final Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
}
