package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common;

import java.util.List;

/**
 * Value object representing a hard-cutoff business rule rejection.
 * Contains domain risk metrics and explainability details.
 *
 * @author Lucía Fernández Mancebo
 * @date 28/06/2026
 */
public class HardCutoffRejection {

    private final String featureName;
    private final Double featureValue;
    private final List<RiskFeature> explainability;

    /**
     * Constructor for HardCutoffRejection.
     *
     * @param featureName    the name of the triggering ratio field.
     * @param featureValue   the value of the triggering ratio.
     * @param explainability the single-item explainability record.
     */
    public HardCutoffRejection(
            final String featureName,
            final Double featureValue,
            final List<RiskFeature> explainability) {
        this.featureName = featureName;
        this.featureValue = featureValue;
        this.explainability = explainability;
    }

    public String getFeatureName() {
        return featureName;
    }

    public Double getFeatureValue() {
        return featureValue;
    }

    public List<RiskFeature> getExplainability() {
        return explainability;
    }
}
