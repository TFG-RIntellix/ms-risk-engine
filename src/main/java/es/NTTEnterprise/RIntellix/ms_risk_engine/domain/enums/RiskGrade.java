package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums;

/**
 * Enum class representing risk grades for credit risk assessment.
 * Defined in the Basilea Regulatory Framework, these grades classify the credit
 * risk of a borrower
 * based on their probability of default and other risk factors.
 * 
 * Needed to have multiple grades to differentiate risk levels more granularly,
 * especially for retail portfolios where PD can vary widely.
 * This is because a PD of 20% (1 in 5 borrowers defaults) is considered high
 * risk, while a PD of 0.1% (1 in 1000 borrowers defaults) is very low risk.
 * 
 * @author Lucía Fernández Mancebo
 * @date 10/05/2026
 */
public enum RiskGrade {
    A,
    B,
    C,
    D,
    E,
    F,
    G
}
