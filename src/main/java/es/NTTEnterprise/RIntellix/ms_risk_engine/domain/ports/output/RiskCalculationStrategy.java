package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RiskGrade;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskGradeCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.RiskCalculationDefaults;

/**
 * Strategy interface for product-specific admission risk calculation logic.
 *
 * Each implementation handles the unique EAD and LGD calculation rules
 * for a specific contract type:
 * - Loans: Principal-based EAD with fixed recovery-rate LGD.
 * - Mortgages: Collateral-based LGD with haircut and foreclosure costs.
 * - Standard Credit Cards: CCF-based EAD with fixed LGD.
 * - Revolving Credit Cards: Higher CCF and LGD reflecting perpetual debt risk.
 *
 * The two-phase design (pre-PD / full assembly) enables parallelization:
 * EAD and LGD can be computed while the asynchronous AI model call is
 * in-flight.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-25-2026
 */
public interface RiskCalculationStrategy {

    /**
     * Indicates whether this strategy supports the given request type.
     *
     * @param requestType the normalized request type.
     * @param isRevolving the revolving flag for credit card disambiguation;
     *                    null for non-credit-card types.
     * @return true when this strategy handles the given combination.
     */
    boolean supports(String requestType, Boolean isRevolving);

    /**
     * Computes exposure at default (EAD) and loss given default (LGD).
     *
     * This method does NOT require the probability of default (PD) and can
     * therefore be executed in parallel with the asynchronous AI model call.
     *
     * @param requestedAmount the requested principal, loan amount, or credit limit.
     * @param ltv             the loan-to-value ratio; null when not applicable.
     * @return a RiskMetrics instance with EAD and LGD populated.
     */
    RiskMetrics calculatePrePdMetrics(Double requestedAmount, Double ltv);

    /**
     * Assembles the final risk metrics once PD is available using
     * RiskGradeCalculator.
     *
     * This method is the preferred implementation that uses the domain service
     * for risk grade calculation, ensuring proper separation of concerns.
     *
     * Combines the pre-computed EAD and LGD with the model-provided PD
     * to produce ECL and the composite RiskGrade using RiskGradeCalculator.
     *
     * @param pd              the probability of default from the AI model.
     * @param prePdMetrics    the pre-computed metrics containing EAD and LGD.
     * @param requestedAmount the requested amount for monthly payment calculation.
     * @param annualIncome    the borrower annual income.
     * @param termMonths      the loan term in months; null for credit cards.
     * @param interestRate    the annual interest rate; null for credit cards.
     * @param gradeCalculator the RiskGradeCalculator domain service.
     * @return the fully assembled RiskMetrics with PD, LGD, EAD, ECL, and
     *         RiskGrade.
     */
    default RiskMetrics assembleFullMetricsWithGradeCalculator(
            final Double pd,
            final RiskMetrics prePdMetrics,
            final Double requestedAmount,
            final Double annualIncome,
            final Integer termMonths,
            final Double interestRate,
            final RiskGradeCalculator gradeCalculator) {

        if (pd == null || prePdMetrics == null || prePdMetrics.getEad() == null
                || prePdMetrics.getLgd() == null) {
            throw new IllegalArgumentException(LogMessage.ERROR_ASSEMBLING_FULL_METRICS + pd);
        }

        double safePd = RiskCalculationDefaults.clampRatio(pd);
        double ead = prePdMetrics.getEad();
        double lgd = prePdMetrics.getLgd();
        double ecl = safePd * lgd * ead * RiskCalculationDefaults.DISCOUNT_FACTOR;

        // Use RiskGradeCalculator for risk grade determination
        RiskGrade riskGrade = gradeCalculator.calculateRiskGrade(
                safePd, ecl, ead, requestedAmount, annualIncome, termMonths, interestRate);

        return new RiskMetrics(safePd, lgd, ead, ecl, riskGrade);
    }
}
