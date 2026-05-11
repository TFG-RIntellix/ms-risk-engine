package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities;

import java.util.Objects;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RiskGrade;

/**
 * Represents the core risk metrics of a scoring execution.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 04-25-2026
 */
public class RiskMetrics {

    private Double probabilityOfDefault;
    private Double lossGivenDefault;
    private Double exposureAtDefault;
    private Double expectedCalculatedLoss;
    private String riskLevel;

    /**
     * Constructor of the RiskMetrics class.
     */
    public RiskMetrics() {
    }

    /**
     * Constructor of the RiskMetrics class.
     *
     * @param probabilityOfDefault   the probability of default.
     * @param lossGivenDefault       the loss given default.
     * @param exposureAtDefault      the exposure at default.
     * @param expectedCalculatedLoss the expected calculated loss.
     * @param riskLevel              the string representation of the risk level.
     */
    public RiskMetrics(final Double probabilityOfDefault,
            final Double lossGivenDefault,
            final Double exposureAtDefault,
            final Double expectedCalculatedLoss,
            final String riskLevel) {
        this.probabilityOfDefault = probabilityOfDefault;
        this.lossGivenDefault = lossGivenDefault;
        this.exposureAtDefault = exposureAtDefault;
        this.expectedCalculatedLoss = expectedCalculatedLoss;
        this.riskLevel = riskLevel;
    }

    /**
     * Constructor of the RiskMetrics class.
     *
     * @param pd        the probability of default.
     * @param lgd       the loss given default.
     * @param ead       the exposure at default.
     * @param ecl       the expected calculated loss.
     * @param riskGrade the risk grade enum.
     */
    public RiskMetrics(final Double pd,
            final Double lgd,
            final Double ead,
            final Double ecl,
            final RiskGrade riskGrade) {
        this.probabilityOfDefault = pd;
        this.lossGivenDefault = lgd;
        this.exposureAtDefault = ead;
        this.expectedCalculatedLoss = ecl;
        this.riskLevel = riskGrade == null ? null : riskGrade.name();
    }

    public Double getProbabilityOfDefault() {
        return probabilityOfDefault;
    }

    public void setProbabilityOfDefault(final Double probabilityOfDefault) {
        this.probabilityOfDefault = probabilityOfDefault;
    }

    public Double getLossGivenDefault() {
        return lossGivenDefault;
    }

    public void setLossGivenDefault(final Double lossGivenDefault) {
        this.lossGivenDefault = lossGivenDefault;
    }

    public Double getExposureAtDefault() {
        return exposureAtDefault;
    }

    public void setExposureAtDefault(final Double exposureAtDefault) {
        this.exposureAtDefault = exposureAtDefault;
    }

    public Double getExpectedCalculatedLoss() {
        return expectedCalculatedLoss;
    }

    public void setExpectedCalculatedLoss(final Double expectedCalculatedLoss) {
        this.expectedCalculatedLoss = expectedCalculatedLoss;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(final String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Double getPd() {
        return probabilityOfDefault;
    }

    public Double getLgd() {
        return lossGivenDefault;
    }

    public Double getEad() {
        return exposureAtDefault;
    }

    public Double getEcl() {
        return expectedCalculatedLoss;
    }

    public RiskGrade getRiskGrade() {
        if (riskLevel == null || riskLevel.isBlank()) {
            return null;
        }
        return RiskGrade.valueOf(riskLevel);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(probabilityOfDefault);
        result = prime * result + Objects.hashCode(lossGivenDefault);
        result = prime * result + Objects.hashCode(exposureAtDefault);
        result = prime * result + Objects.hashCode(expectedCalculatedLoss);
        result = prime * result + Objects.hashCode(riskLevel);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final RiskMetrics other = (RiskMetrics) obj;
        return Objects.equals(probabilityOfDefault, other.probabilityOfDefault)
                && Objects.equals(lossGivenDefault, other.lossGivenDefault)
                && Objects.equals(exposureAtDefault, other.exposureAtDefault)
                && Objects.equals(expectedCalculatedLoss, other.expectedCalculatedLoss)
                && Objects.equals(riskLevel, other.riskLevel);
    }

    @Override
    public String toString() {
        return "RiskMetrics{" +
                "probabilityOfDefault=" + probabilityOfDefault +
                ", lossGivenDefault=" + lossGivenDefault +
                ", exposureAtDefault=" + exposureAtDefault +
                ", expectedCalculatedLoss=" + expectedCalculatedLoss +
                ", riskLevel='" + riskLevel + '\'' +
                '}';
    }
}
