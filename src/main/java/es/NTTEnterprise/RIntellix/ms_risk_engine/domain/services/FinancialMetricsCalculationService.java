package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.FinancialMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.FinancialMetricsCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;

/**
 * Domain service for calculating financial affordability metrics.
 * 
 * Encapsulates the calculation of financial metrics used to assess customer
 * affordability and payment obligations for a credit product.
 * 
 * Responsibilities:
 * - Calculate monthly payment using amortization formula
 * - Calculate debt-to-income ratio
 * - Calculate total payment over the credit term
 * - Calculate total interest cost
 * - Calculate monthly disposable income after obligations
 * 
 * This service delegates to FinancialMetricsCalculator for individual
 * metric calculations and assembles them into a complete FinancialMetrics
 * value object.
 * 
 * Usage:
 * - Called during scoring to compute financial metrics for the original request
 * - Called during simulation to compute financial metrics for what-if scenarios
 * - Can be reused for any financial metric computation within the system
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-26-2026
 */
@Slf4j
public class FinancialMetricsCalculationService {

        /**
         * Calculates complete financial metrics for a credit product.
         * 
         * Computes all affordability metrics in a single operation to ensure
         * consistency across related calculations.
         *
         * @param principal           the principal amount (loan/mortgage amount)
         * @param annualInterestRate  the annual nominal interest rate (percentage,
         *                            e.g., 5.0 for 5%)
         * @param termMonths          the loan term in months
         * @param annualIncome        the customer's annual income (for DTI and
         *                            disposable income)
         * @param existingObligations the customer's existing monthly financial
         *                            obligations (for DTI)
         * @return a complete FinancialMetrics object with all metrics calculated
         * @throws IllegalArgumentException if any required parameter is null or invalid
         */
        public FinancialMetrics calculateFinancialMetrics(
                        final Double principal,
                        final Double annualInterestRate,
                        final Integer termMonths,
                        final Double annualIncome,
                        final Double existingObligations) {

                // Validate inputs
                Objects.requireNonNull(principal, LogMessage.PRINCIPAL_AMOUNT_CANNOT_BE_NULL);
                Objects.requireNonNull(annualInterestRate, LogMessage.ANNUAL_INTEREST_RATE_CANNOT_BE_NULL);
                Objects.requireNonNull(termMonths, LogMessage.TERM_MONTHS_CANNOT_BE_NULL);
                Objects.requireNonNull(annualIncome, LogMessage.ANNUAL_INCOME_CANNOT_BE_NULL);
                Objects.requireNonNull(existingObligations, LogMessage.EXISTING_OBLIGATIONS_CANNOT_BE_NULL);

                log.info(LogMessage.FINANCIAL_METRICS_CALCULATION_START,
                                principal, annualInterestRate, termMonths, annualIncome, existingObligations);

                // Step 1: Calculate monthly payment
                final double monthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(
                                principal,
                                annualInterestRate,
                                termMonths);

                // Step 2: Calculate total debt-to-income ratio (existing + new payment)
                final double totalMonthlyObligations = existingObligations + monthlyPayment;
                final double dti = FinancialMetricsCalculator.calculateDti(
                                totalMonthlyObligations,
                                annualIncome);

                // Step 3: Calculate total payment over the term
                final double totalPayment = FinancialMetricsCalculator.calculateTotalPayment(
                                monthlyPayment,
                                termMonths);

                // Step 4: Calculate total interest cost
                final double totalInterest = FinancialMetricsCalculator.calculateTotalInterest(
                                totalPayment,
                                principal);

                // Step 5: Calculate monthly disposable income after obligations
                final double monthlyDisposableIncome = FinancialMetricsCalculator.calculateDisposableIncome(
                                annualIncome,
                                totalMonthlyObligations);

                log.debug(LogMessage.FINANCIAL_METRICS_CALCULATION_COMPLETE,
                                monthlyPayment, dti, totalPayment, totalInterest, monthlyDisposableIncome);

                // Assemble into FinancialMetrics value object
                return new FinancialMetrics(
                                monthlyPayment,
                                dti,
                                totalPayment,
                                totalInterest,
                                monthlyDisposableIncome);
        }

        /**
         * Calculates financial metrics without existing obligations (simplified DTI).
         * 
         * Used when existing obligations are not available or not required.
         * DTI will be calculated based only on the new monthly payment.
         *
         * @param principal          the principal amount
         * @param annualInterestRate the annual nominal interest rate (percentage)
         * @param termMonths         the loan term in months
         * @param annualIncome       the customer's annual income
         * @return a complete FinancialMetrics object
         */
        public FinancialMetrics calculateFinancialMetricsWithoutExistingObligations(
                        final Double principal,
                        final Double annualInterestRate,
                        final Integer termMonths,
                        final Double annualIncome) {

                return calculateFinancialMetrics(principal, annualInterestRate, termMonths, annualIncome, 0.0);
        }
}
