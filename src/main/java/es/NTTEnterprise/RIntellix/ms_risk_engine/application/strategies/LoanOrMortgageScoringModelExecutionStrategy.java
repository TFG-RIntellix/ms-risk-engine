package es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationPayload;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.ScoringModelExecutionResultDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.LoanOrMortgageModelPayloadMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases.ScoringModelInvocationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.RiskCalculationStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.RiskCalculationStrategyFactory;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

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
@Component
@Slf4j
public class LoanOrMortgageScoringModelExecutionStrategy implements ScoringModelExecutionStrategy {

        private static final Set<String> SUPPORTED_REQUEST_TYPES = Set.of(
                        "PRESTAMO",
                        "HIPOTECA");

        private final LoanOrMortgageModelPayloadMapper payloadMapper;
        private final ScoringModelInvocationService modelInvocationService;
        private final List<RiskCalculationStrategy> riskCalculationStrategies;
        private final String predictLoanPath;

        /**
         * Constructor of the LoanOrMortgageScoringModelExecutionStrategy class.
         *
         * @param payloadMapper             the mapper that prepares loan model payload.
         * @param modelInvocationService    the service that executes model calls.
         * @param riskCalculationStrategies the available risk calculation strategies.
         * @param predictLoanPath           the model endpoint path for loan and
         *                                  mortgage.
         */
        public LoanOrMortgageScoringModelExecutionStrategy(
                        final LoanOrMortgageModelPayloadMapper payloadMapper,
                        final ScoringModelInvocationService modelInvocationService,
                        final List<RiskCalculationStrategy> riskCalculationStrategies,
                        @Value("${risk.model.predict-loan-path:/api/v1/risk/predict-loan}") final String predictLoanPath) {
                this.payloadMapper = Objects.requireNonNull(payloadMapper);
                this.modelInvocationService = Objects.requireNonNull(modelInvocationService);
                this.riskCalculationStrategies = Objects.requireNonNull(riskCalculationStrategies);
                this.predictLoanPath = Objects.requireNonNull(predictLoanPath);
        }

        @Override
        public boolean supports(final String requestType) {
                return requestType != null && SUPPORTED_REQUEST_TYPES.contains(requestType);
        }

        @Override
        public ScoringModelExecutionResultDTO executePredictionModel(
                        final ScoringGenerationPayload payload,
                        final String requestType,
                        final String requestId) {

                if (!(payload instanceof ScoringGenerationRequest request)) {
                        throw new IllegalArgumentException(LogMessage.STRATEGY_ERROR_PAYLOAD);
                }

                // Map payload to model request format.
                final Map<String, Object> modelRequestPayload = payloadMapper.toModelPayload(request);

                // Call Model
                final CompletableFuture<ModelPredictionResult> predictionResultFuture = modelInvocationService
                                .invokePrediction(
                                                modelRequestPayload,
                                                requestId,
                                                predictLoanPath);

                // Compute Pre-PD Metrics
                final RiskCalculationStrategy riskStrategy = RiskCalculationStrategyFactory.createStrategy(
                                requestType, null, riskCalculationStrategies);
                final RiskMetrics prePdMetrics = riskStrategy.calculatePrePdMetrics(
                                request.getLoanAmount(), request.getLtv());

                log.info(LogMessage.PRE_PD_METRICS_COMPUTED, requestId, prePdMetrics.getEad(), prePdMetrics.getLgd());

                // Wait until obtaining PD result.
                final ModelPredictionResult predictionResult = predictionResultFuture.join();
                log.info(LogMessage.MODEL_PREDICTION_RESULT, requestId, predictionResult.getProbabilityOfDefault());
                log.info(LogMessage.MODEL_PREDICTION_RESULT_TO_STRING, predictionResult.toString());

                // Assemble full risk metrics with PD, ECL, and RiskGrade.
                final RiskMetrics fullMetrics = riskStrategy.assembleFullMetrics(
                                predictionResult.getProbabilityOfDefault(),
                                prePdMetrics,
                                request.getLoanAmount(),
                                request.getAnnualIncome(),
                                request.getTermMonths(),
                                request.getInterestRate());

                return new ScoringModelExecutionResultDTO(modelRequestPayload, predictionResult, fullMetrics);
        }
}
