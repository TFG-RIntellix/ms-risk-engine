package es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies;

import java.util.Optional;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CreditCardScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationPayload;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.ScoringModelExecutionResultDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.CreditCardModelPayloadMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.services.RiskMetricsCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskMetricsCalculationContext;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RequestType;
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
@Slf4j
@Component
public class CreditCardScoringModelExecutionStrategy implements ScoringModelExecutionStrategy {

        private final CreditCardModelPayloadMapper payloadMapper;
        private final RiskMetricsCalculationService metricsCalculationService;
        private final String predictCreditCardPath;

        /**
         * Constructor of the CreditCardScoringModelExecutionStrategy class.
         *
         * @param payloadMapper             the mapper that prepares credit-card model
         *                                  payload.
         * @param metricsCalculationService the service that orchestrates risk metric
         *                                  calculation.
         * @param predictCreditCardPath     the model endpoint path for credit cards.
         */
        public CreditCardScoringModelExecutionStrategy(
                        final CreditCardModelPayloadMapper payloadMapper,
                        final RiskMetricsCalculationService metricsCalculationService,
                        @Value("${risk.model.predict-credit-card-path:/api/v1/risk/predict-credit-card}") final String predictCreditCardPath) {
                this.payloadMapper = Objects.requireNonNull(payloadMapper);
                this.metricsCalculationService = Objects.requireNonNull(metricsCalculationService);
                this.predictCreditCardPath = Objects.requireNonNull(predictCreditCardPath);
        }

        @Override
        public boolean supports(final String requestType) {
                try {
                        return RequestType.fromValue(requestType) == RequestType.TARJETA_CREDITO;
                } catch (IllegalArgumentException e) {
                        return false;
                }
        }

        @Override
        public ScoringModelExecutionResultDTO executePredictionModel(
                        final ScoringGenerationPayload payload,
                        final String requestType,
                        final String requestId) {

                if (!(payload instanceof CreditCardScoringGenerationRequest request)) {
                        throw new IllegalArgumentException(LogMessage.CREDIT_CARD_STRATEGY_ERROR_PAYLOAD);
                }

                // Step 1: Map payload to model request format
                final Map<String, Object> modelRequestPayload = payloadMapper.toModelPayload(request, requestType);

                // Step 2: Get isRevolving flag to select correct strategy (Standard vs
                // Revolving)
                // This flag is needed to resolve the correct risk calculation strategy
                // before creating the calculation context
                // Determine revolving flag (used by risk strategies if needed)
                final Boolean isRevolving = request.getIsRevolving();

                // Step 3: Create calculation context with all necessary data
                final RiskMetricsCalculationContext context = new RiskMetricsCalculationContext(
                                modelRequestPayload,
                                requestId,
                                predictCreditCardPath,
                                requestType);

                // Step 4: DELEGATE core calculation to reusable service
                // This service handles:
                // - Async model invocation
                // - Parallel pre-PD metrics calculation (EAD/LGD)
                // - Full metrics assembly with ECL and risk grade
                final var result = metricsCalculationService.calculateRiskMetrics(context);
                final RiskMetrics fullMetrics = result.riskMetrics();
                final ModelPredictionResult prediction = result.modelPredictionResult();

                log.info(LogMessage.MODEL_EXECUTION_RESULT_CREDIT_CARD,
                                fullMetrics.getProbabilityOfDefault());

                return new ScoringModelExecutionResultDTO(modelRequestPayload, prediction, fullMetrics, result.isHardCutoff());
        }

        @Override
        public String modelEndpointPath() {
                return this.predictCreditCardPath;
        }
}
