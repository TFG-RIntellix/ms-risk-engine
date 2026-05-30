package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums;

/**
 * Centralized risk thresholds used across the risk calculation logic.
 */
public enum RiskThresholds {
    DTI_MAX(0.45),
    LTV_MAX(0.80),
    PD_MIN(0.01),
    PD_MAX(0.99);

    private final double value;

    RiskThresholds(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
