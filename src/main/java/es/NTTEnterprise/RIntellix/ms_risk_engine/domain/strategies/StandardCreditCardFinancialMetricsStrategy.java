package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies;

import java.util.Objects;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.FinancialMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RequestType;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.CreditCardFinancialMetricsCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Financial metrics strategy for standard (non-revolving) credit cards.
 * Standard cards require full payment at the end of the month, zero interest.
 *
 * @author Lucía Fernández Mancebo
 */
@Slf4j
public class StandardCreditCardFinancialMetricsStrategy implements FinancialMetricsStrategy {

    @Override
    public boolean supports(String requestType, Boolean isRevolving) {
        try {
            RequestType type = RequestType.fromValue(requestType);
            return type == RequestType.TARJETA_CREDITO && Boolean.FALSE.equals(isRevolving);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public FinancialMetrics calculateFinancialMetrics(
            Double amount,
            Double interestRate,
            Double annualIncome,
            Double existingObligations,
            Integer termMonths) {

        Objects.requireNonNull(amount, LogMessage.PRINCIPAL_AMOUNT_CANNOT_BE_NULL);
        Objects.requireNonNull(annualIncome, LogMessage.ANNUAL_INCOME_CANNOT_BE_NULL);

        log.debug(LogMessage.CC_FINANCIAL_METRICS_CALCULATION_START);

        // For standard cards, monthly payment is the full credit limit
        final double monthlyPayment = CreditCardFinancialMetricsCalculator.calculateStandardMonthlyPayment(amount);
        
        // DTI does not include existing obligations here because they are added later in DtiCalculationService for new scoring
        // However, for simulations, we calculate purely the new DTI based on this new payment
        final double dti = CreditCardFinancialMetricsCalculator.calculateCreditCardDti(monthlyPayment, annualIncome);
        
        final double totalPayment = amount; // Always pays exactly the limit
        final double totalInterest = 0.0; // Standard cards don't accrue interest if paid in full
        
        final double disposableIncome = CreditCardFinancialMetricsCalculator.calculateCreditCardDisposableIncome(annualIncome, monthlyPayment);

        log.debug(LogMessage.FINANCIAL_METRICS_CALCULATION_COMPLETE,
                monthlyPayment, dti, totalPayment, totalInterest, disposableIncome);

        final FinancialMetrics metrics = new FinancialMetrics();
        metrics.setMonthlyPayment(monthlyPayment);
        metrics.setDebtToIncomeRatio(dti);
        metrics.setTotalPayment(totalPayment);
        metrics.setTotalInterest(totalInterest);
        metrics.setMonthlyDisposableIncome(disposableIncome);

        return metrics;
    }
}
