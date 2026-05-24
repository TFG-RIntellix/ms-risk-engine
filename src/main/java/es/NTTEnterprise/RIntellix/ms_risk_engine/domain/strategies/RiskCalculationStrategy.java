package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskGradeCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RiskGrade;

/**
 * Strategy interface for product-specific admission risk calculation logic.
 *
 * Moved to domain.strategies to reflect that this is a domain internal
 * strategy, not an external output port.
 */
public interface RiskCalculationStrategy {

    boolean supports(String requestType, Boolean isRevolving);

    RiskMetrics calculatePrePdMetrics(Double requestedAmount, Double ltv);

    /**
     * Assemble full RiskMetrics combining model PD with pre-PD metrics (EAD, LGD)
     * and calculating ECL and risk grade using the provided calculator.
     *
     * A default implementation is provided so concrete strategies only need to
     * implement pre-PD computation and support resolution.
     */
    default RiskMetrics assembleFullMetricsWithGradeCalculator(
            final Double probabilityOfDefault,
            final RiskMetrics prePdMetrics,
            final Double amount,
            final Double annualIncome,
            final Integer termMonths,
            final Double interestRate,
            final RiskGradeCalculator riskGradeCalculator) {

        final double pd = probabilityOfDefault == null ? 0.0 : probabilityOfDefault;
        final double ead = prePdMetrics == null || prePdMetrics.getExposureAtDefault() == null
                ? 0.0
                : prePdMetrics.getExposureAtDefault();
        final double lgd = prePdMetrics == null || prePdMetrics.getLossGivenDefault() == null
                ? 0.0
                : prePdMetrics.getLossGivenDefault();

        final double ecl = pd * lgd * ead;

        final RiskMetrics metrics = new RiskMetrics();
        metrics.setProbabilityOfDefault(pd);
        metrics.setExposureAtDefault(ead);
        metrics.setLossGivenDefault(lgd);
        metrics.setExpectedCalculatedLoss(ecl);

        try {
            final RiskGrade grade = riskGradeCalculator.calculateRiskGrade(pd, ecl, ead,
                    amount == null ? 0.0 : amount,
                    annualIncome,
                    termMonths,
                    interestRate);
            metrics.setRiskLevel(grade == null ? null : grade.name());
        } catch (Exception ex) {
            // In case of any grade calculation issue, leave risk level null
            metrics.setRiskLevel(null);
        }

        return metrics;
    }

}
