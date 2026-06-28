package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import java.util.Objects;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;

/**
 * Result object from risk metrics calculation.
 * 
 * Wraps both the model prediction result and the fully assembled risk metrics.
 * This object is returned by the risk metrics calculation service and contains
 * all the data needed by the application layer for further processing.
 *
 * This pattern follows the Value Object pattern to aggregate calculation
 * outputs
 * in a single, immutable structure.
 *
 * Properties:
 * - modelPredictionResult: The raw prediction from the ML model (contains PD)
 * - riskMetrics: The fully assembled risk metrics (includes PD, EAD, LGD, ECL,
 * risk grade)
 *
 * @author Lucia Fernandez Mancebo
 * @Date 09-05-2026
 */
public record RiskMetricsCalculationResult(
        ModelPredictionResult modelPredictionResult,
        RiskMetrics riskMetrics,
        boolean isHardCutoff) {

    public RiskMetricsCalculationResult {
        Objects.requireNonNull(modelPredictionResult, "Model prediction result cannot be null");
        Objects.requireNonNull(riskMetrics, "Risk metrics cannot be null");
    }

}
