package es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CreditCardScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationPayload;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.ScoringModelExecutionResultDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.CreditCardModelPayloadMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases.ScoringModelInvocationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.RiskCalculationStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.RiskCalculationStrategyFactory;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Strategy for credit card model execution with parallelized risk calculation.
 *
 * Execution flow:
 * 1. Map payload to model request format.
 * 2. Fire async AI model call (returns CompletableFuture).
 * 3. While model call is in-flight: resolve Standard vs Revolving risk strategy
 * using isRevolving flag, then compute EAD and LGD.
 * 4. Join model future to obtain PD.
 * 5. Assemble full risk metrics (ECL, RiskGrade) combining PD with pre-computed
 * EAD/LGD.
 *
 * @author Lucía Fernández Mancebo
 * @Date 04-25-2026
 */
@Component
@Slf4j
public class CreditCardScoringModelExecutionStrategy implements ScoringModelExecutionStrategy {

        private static final Set<String> SUPPORTED_REQUEST_TYPES = Set.of(
                        "TARJETA_CREDITO");

        private final CreditCardModelPayloadMapper payloadMapper;
        private final ScoringModelInvocationService modelInvocationService;
        private final List<RiskCalculationStrategy> riskCalculationStrategies;
        private final String predictCreditCardPath;

        /**
         * Constructor of the CreditCardScoringModelExecutionStrategy class.
         *
         * @param payloadMapper             the mapper that prepares credit-card model
         *                                  payload.
         * @param modelInvocationService    the service that executes model calls.
         * @param riskCalculationStrategies the available risk calculation strategies.
         * @param predictCreditCardPath     the model endpoint path for credit cards.
         */
        public CreditCardScoringModelExecutionStrategy(
                        final CreditCardModelPayloadMapper payloadMapper,
                        final ScoringModelInvocationService modelInvocationService,
                        final List<RiskCalculationStrategy> riskCalculationStrategies,
                        @Value("${risk.model.predict-credit-card-path:/api/v1/risk/predict-credit-card}") final String predictCreditCardPath) {
                this.payloadMapper = Objects.requireNonNull(payloadMapper);
                this.modelInvocationService = Objects.requireNonNull(modelInvocationService);
                this.riskCalculationStrategies = Objects.requireNonNull(riskCalculationStrategies);
                this.predictCreditCardPath = Objects.requireNonNull(predictCreditCardPath);
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

                if (!(payload instanceof CreditCardScoringGenerationRequest request)) {
                        throw new IllegalArgumentException(LogMessage.CREDIT_CARD_STRATEGY_ERROR_PAYLOAD);
                }

                // Map payload to model request format.
                final Map<String, Object> modelRequestPayload = payloadMapper.toModelPayload(request, requestType);

                // Call Model.
                final CompletableFuture<ModelPredictionResult> predictionResultFuture = modelInvocationService
                                .invokePrediction(
                                                modelRequestPayload,
                                                requestId,
                                                predictCreditCardPath);

                // Compute Pre-PD Metrics.
                final Boolean isRevolving = request.getIsRevolving();
                final RiskCalculationStrategy riskStrategy = RiskCalculationStrategyFactory.createStrategy(
                                requestType, isRevolving, riskCalculationStrategies);

                final RiskMetrics prePdMetrics = riskStrategy.calculatePrePdMetrics(
                                request.getCreditLimit(), null);
                log.debug(LogMessage.PRE_PD_METRICS_COMPUTED, requestId, prePdMetrics.getEad(), prePdMetrics.getLgd());

                // Wait until obtaining PD result.
                final ModelPredictionResult predictionResult = predictionResultFuture.join();
                log.debug(LogMessage.MODEL_PREDICTION_RESULT, requestId,
                                predictionResult.getProbabilityOfDefault());

                // Assemble full risk metrics with PD, ECL, and RiskGrade.
                final RiskMetrics fullMetrics = riskStrategy.assembleFullMetrics(
                                predictionResult.getProbabilityOfDefault(),
                                prePdMetrics,
                                request.getCreditLimit(),
                                request.getAnnualIncome(),
                                null,
                                null);

                return new ScoringModelExecutionResultDTO(modelRequestPayload, predictionResult, fullMetrics);
        }
}
