package es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies;

import java.util.Optional;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationPayload;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.ScoringModelExecutionResultDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.LoanOrMortgageModelPayloadMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.services.RiskMetricsCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.HardCutoffRejection;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RequestType;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.HardCutoffRuleEvaluator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskMetricsCalculationContext;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Strategy for loan and mortgage model execution with parallelized risk
 * calculation.
 *
 * Execution flow:
 * 1. Map payload to model request format.
 * 2. Fire async AI model call (returns CompletableFuture).
 * 3. While model call is in-flight: compute EAD and LGD via risk calculation
 * strategy.
 * 4. Join model future to obtain PD.
 * 5. Assemble full risk metrics (ECL, RiskGrade) combining PD with pre-computed
 * EAD/LGD.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-25-2026
 */
@Slf4j
@Component
public class LoanOrMortgageScoringModelExecutionStrategy implements ScoringModelExecutionStrategy {

        private final LoanOrMortgageModelPayloadMapper payloadMapper;
        private final RiskMetricsCalculationService metricsCalculationService;
        private final HardCutoffRuleEvaluator hardCutoffRuleEvaluator;
        private final String predictLoanPath;

        /**
         * Constructor of the LoanOrMortgageScoringModelExecutionStrategy class.
         *
         * @param payloadMapper             the mapper that prepares loan model payload.
         * @param metricsCalculationService the service that orchestrates risk metric
         *                                  calculation.
         * @param hardCutoffRuleEvaluator   the domain service for hard-cutoff rule
         *                                  evaluation.
         * @param predictLoanPath           the model endpoint path for loan and
         *                                  mortgage.
         */
        public LoanOrMortgageScoringModelExecutionStrategy(
                        final LoanOrMortgageModelPayloadMapper payloadMapper,
                        final RiskMetricsCalculationService metricsCalculationService,
                        final HardCutoffRuleEvaluator hardCutoffRuleEvaluator,
                        @Value("${risk.model.predict-loan-path:/api/v1/risk/predict-loan}") final String predictLoanPath) {
                this.payloadMapper = Objects.requireNonNull(payloadMapper);
                this.metricsCalculationService = Objects.requireNonNull(metricsCalculationService);
                this.hardCutoffRuleEvaluator = Objects.requireNonNull(hardCutoffRuleEvaluator);
                this.predictLoanPath = Objects.requireNonNull(predictLoanPath);
        }

        @Override
        public boolean supports(final String requestType) {
                try {
                        RequestType type = RequestType.fromValue(requestType);
                        return type == RequestType.PRESTAMO || type == RequestType.HIPOTECA;
                } catch (IllegalArgumentException e) {
                        return false;
                }
        }

        @Override
        public ScoringModelExecutionResultDTO executePredictionModel(
                        final ScoringGenerationPayload payload,
                        final String requestType,
                        final String requestId) {

                if (!(payload instanceof ScoringGenerationRequest request)) {
                        throw new IllegalArgumentException(LogMessage.STRATEGY_ERROR_PAYLOAD);
                }

                // Step 1: Map payload to model request format
                final Map<String, Object> modelRequestPayload = payloadMapper.toModelPayload(request);

                // Step 1.5: Evaluate mapped hard-cutoff rules
                final Optional<HardCutoffRejection> rejectionOpt = hardCutoffRuleEvaluator.evaluateRules(
                                modelRequestPayload,
                                requestType,
                                requestId);
                if (rejectionOpt.isPresent()) {
                        final HardCutoffRejection rejection = rejectionOpt.get();
                        final ModelPredictionResult prediction = new ModelPredictionResult(
                                        1.0,
                                        "HIGH",
                                        0.0,
                                        rejection.getExplainability());
                        return new ScoringModelExecutionResultDTO(modelRequestPayload, prediction,
                                        rejection.getRiskMetrics());
                }

                // Step 2: Create calculation context with all necessary data
                final RiskMetricsCalculationContext context = new RiskMetricsCalculationContext(
                                modelRequestPayload,
                                requestId,
                                predictLoanPath,
                                requestType);

                // Step 3: DELEGATE core calculation to reusable service
                // This service handles:
                // - Async model invocation
                // - Parallel pre-PD metrics calculation
                // - Full metrics assembly with ECL and risk grade
                final var result = metricsCalculationService.calculateRiskMetrics(context);
                final RiskMetrics fullMetrics = result.riskMetrics();
                final ModelPredictionResult prediction = result.modelPredictionResult();

                log.info(LogMessage.MODEL_EXECUTION_RESULT_LOAN_MORTGAGE,
                                fullMetrics.getProbabilityOfDefault());

                return new ScoringModelExecutionResultDTO(modelRequestPayload, prediction, fullMetrics);
        }

        @Override
        public String modelEndpointPath() {
                return this.predictLoanPath;
        }
}
