package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output;

/**
 * Output DTO representing the core risk metrics of a scoring result
 * for Kafka message transport.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-26-2026
 */
public class RiskMetricsDTO {

    private Double probabilityOfDefault;
    private Double lossGivenDefault;
    private Double exposureAtDefault;
    private Double expectedCalculatedLoss;
    private String riskLevel;

    /**
     * Constructor of the RiskMetricsDTO class.
     */
    public RiskMetricsDTO() {
    }

    /**
     * Constructor of the RiskMetricsDTO class.
     *
     * @param probabilityOfDefault   the probability of default.
     * @param lossGivenDefault       the loss given default.
     * @param exposureAtDefault      the exposure at default.
     * @param expectedCalculatedLoss the expected calculated loss.
     * @param riskLevel              the string representation of the risk level.
     */
    public RiskMetricsDTO(final Double probabilityOfDefault,
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

    // Getters and setters.

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

    @Override
    public String toString() {
        return "RiskMetricsDTO [probabilityOfDefault=" + probabilityOfDefault + ", lossGivenDefault="
                + lossGivenDefault + ", exposureAtDefault=" + exposureAtDefault + ", expectedCalculatedLoss="
                + expectedCalculatedLoss + ", riskLevel=" + riskLevel + "]";
    }
}
