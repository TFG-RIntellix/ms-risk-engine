package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common;

import java.util.List;

/**
 * Value object representing a hard-cutoff business rule rejection.
 * Contains domain risk metrics and explainability details.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 06-28-2026
 */
public class HardCutoffRejection {

    private final String featureName;
    private final Double featureValue;
    private final RiskMetrics riskMetrics;
    private final List<RiskFeature> explainability;

    /**
     * Constructor for HardCutoffRejection.
     *
     * @param featureName    the name of the triggering ratio field.
     * @param featureValue   the value of the triggering ratio.
     * @param riskMetrics    the Basel II calculated risk metrics.
     * @param explainability the single-item explainability record.
     */
    public HardCutoffRejection(
            final String featureName,
            final Double featureValue,
            final RiskMetrics riskMetrics,
            final List<RiskFeature> explainability) {
        this.featureName = featureName;
        this.featureValue = featureValue;
        this.riskMetrics = riskMetrics;
        this.explainability = explainability;
    }

    public String getFeatureName() {
        return featureName;
    }

    public Double getFeatureValue() {
        return featureValue;
    }

    public RiskMetrics getRiskMetrics() {
        return riskMetrics;
    }

    public List<RiskFeature> getExplainability() {
        return explainability;
    }
}
