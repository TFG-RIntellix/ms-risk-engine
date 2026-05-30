package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import java.util.Objects;

import org.springframework.stereotype.Component;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.MathUtilities;

/**
 * Domain service for calculating loan payment scenarios.
 * Encapsulates amortization and payment calculation logic for various loan types.
 *
 * Responsibilities:
 * - Calculate fixed monthly payments using French amortization method (annuity)
 * - Handle edge cases (zero interest rates, invalid terms)
 * - Provide consistent payment calculations across the application
 *
 * This service applies the following formulas:
 * - French Amortization: P = [r * PV] / [1 - (1 + r)^-n]
 * where: P = monthly payment, r = monthly interest rate, PV = principal, n = number of months
 *
 * @author Lucia Fernandez Mancebo
 * @Date 09-05-2026
 */
public class LoanPaymentCalculator {

    /**
     * Calculates the fixed monthly payment for a loan using French amortization method.
     * French amortization results in a constant monthly payment (principal + interest)
     * that covers the entire loan over the specified term.
     *
     * Formula: P = [r * PV] / [1 - (1 + r)^-n]
     * where:
     * - P = monthly payment amount
     * - r = monthly interest rate (annual rate / 12 / 100)
     * - PV = principal (loan amount)
     * - n = number of monthly payments
     *
     * Edge cases handled:
     * - Zero interest rate: payment = principal / term months
     * - Null or invalid term: defaults to 1 month
     * - Null or invalid rate: treated as 0% annual rate
     * - Invalid denominator: falls back to simple division
     *
     * @param principalAmount    the loan principal amount.
     * @param termMonths         the loan term in months (must be > 0).
     * @param annualInterestRate the annual nominal interest rate as a percentage
     *                           (e.g., 5.0 for 5% annual rate).
     * @return the calculated monthly payment amount.
     * @throws NullPointerException if principalAmount or termMonths is null.
     */
    public double calculateFrenchMonthlyPayment(
            final Double principalAmount,
            final Integer termMonths,
            final Double annualInterestRate) {

        Objects.requireNonNull(principalAmount, LogMessage.PRINCIPAL_AMOUNT_CANNOT_BE_NULL);
        Objects.requireNonNull(termMonths, LogMessage.TERM_MONTHS_CANNOT_BE_NULL);

        // Validate and normalize principal
        final double amount = principalAmount <= 0.0 ? 0.0 : principalAmount;

        // Validate and normalize term
        final int terms = termMonths <= 0 ? 1 : termMonths;

        // Validate and normalize annual interest rate, convert to monthly decimal rate
        final double annualRate = annualInterestRate == null ? 0.0 : annualInterestRate;
        final double monthlyRate = annualRate / 1200.0; // Convert to monthly decimal (e.g., 5% → 0.05 → 0.00417)

        // Handle zero interest rate: simple division of principal by term
        if (monthlyRate == 0.0) {
            return MathUtilities.roundFinal(amount / terms);
        }

        // Apply French amortization formula
        final double totalAmount = amount * monthlyRate;
        final double denominator = 1.0 - Math.pow(1.0 + monthlyRate, -terms);

        // Handle edge case where denominator is zero (should rarely occur)
        if (denominator == 0.0) {
            return MathUtilities.roundFinal(totalAmount / terms);
        }

        return MathUtilities.roundFinal(totalAmount / denominator);
    }
}
