package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RequestType;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.RiskCalculationStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.RiskCalculationDefaults;

/**
 * Risk calculation strategy for fixed-rate personal/consumer loans.
 *
 * Business logic: Unsecured debt with a predefined repayment schedule.
 * Risk is concentrated on the borrower's cash flow.
 *
 * EAD = Requested Principal Amount + Financed Fees (default 0.0).
 * LGD = 1 - Recovery Rate = 0.70 (European retail average for unsecured loans).
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-25-2026
 */
public class LoanRiskCalculationStrategy implements RiskCalculationStrategy {

    @Override
    public boolean supports(final String requestType, final Boolean isRevolving) {
        try {
            return RequestType.fromValue(requestType) == RequestType.PRESTAMO;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public RiskMetrics calculatePrePdMetrics(final Double requestedAmount, final Double ltv)
            throws IllegalArgumentException {
        Double safeCreditLimit = null;

        safeCreditLimit = RiskCalculationDefaults.validateRequestAmount(requestedAmount);

        // EAD will be the lost money taking into account we are evaluating the
        // request of the product before giving it to the customer.
        double ead = safeCreditLimit;

        final RiskMetrics metrics = new RiskMetrics();
        metrics.setExposureAtDefault(ead);
        metrics.setLossGivenDefault(RiskCalculationDefaults.LOAN_LGD);
        return metrics;
    }
}
