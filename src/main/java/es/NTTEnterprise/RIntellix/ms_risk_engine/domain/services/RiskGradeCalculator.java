package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import java.util.Objects;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RiskGrade;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.RiskCalculationDefaults;

/**
 * Domain service for calculating risk grades based on multiple risk factors.
 * This service encapsulates the business logic for risk grade determination
 * using a multi-factor scoring model.
 *
 * Responsibilities:
 * - Classify probability of default into risk grades (A, B, C, D, E, F & G)
 * - Apply Basel III compliance rules
 *
 * Risk Grade Algorithm:
 * 1. Base grade from PD thresholds (A/B/C/D/E/F/G)
 * 
 * @author Lucia Fernandez Mancebo
 * @Date 09-05-2026
 */
public class RiskGradeCalculator {

    /**
     * Default constructor for RiskGradeCalculator.
     */
    public RiskGradeCalculator() {
    }

    /**
     * Maps the probability of default (PD) to a base risk grade index based on
     * defined thresholds.
     * The thresholds are defined in RiskCalculationDefaults and follow the Basilea
     * Principles for differentiating risk grades.
     * 
     * @param pd the probability of default.
     * @return the grade index (0=A, 1=B, 2=C, 3=D).
     * @throws NullPointerException if pd is null.
     */
    public RiskGrade calculateRiskGrade(final double pd) {

        Objects.requireNonNull(pd, LogMessage.PROBABILITY_OF_DEFAULT_CANNOT_BE_NULL);

        if (pd < RiskCalculationDefaults.PD_THRESHOLD_GRADE_A) {
            return RiskGrade.A;
        } else if (pd < RiskCalculationDefaults.PD_THRESHOLD_GRADE_B) {
            return RiskGrade.B;
        } else if (pd < RiskCalculationDefaults.PD_THRESHOLD_GRADE_C) {
            return RiskGrade.C;
        } else if (pd < RiskCalculationDefaults.PD_THRESHOLD_GRADE_D) {
            return RiskGrade.D;
        } else if (pd < RiskCalculationDefaults.PD_THRESHOLD_GRADE_E) {
            return RiskGrade.E;
        } else if (pd < RiskCalculationDefaults.PD_THRESHOLD_GRADE_F) {
            return RiskGrade.F;
        } else {
            return RiskGrade.G;
        }
    }
}