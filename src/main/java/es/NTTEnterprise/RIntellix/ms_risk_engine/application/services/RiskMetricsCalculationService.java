package es.NTTEnterprise.RIntellix.ms_risk_engine.application.services;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.FinancialMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.ModelPredictionPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.FinancialMetricsCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.RiskCalculationStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskGradeCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskMetricsCalculationContext;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskMetricsCalculationResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.RiskCalculationStrategyFactory;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

/**
 * Core service for calculating risk metrics.
 * 
 * Encapsulates the common logic for model invocation and risk metric
 * calculation
 * that is reused across both scoring and simulation workflows.
 *
 * Responsibilities:
 * - Orchestrate async model invocation
 * - Parallel computation of pre-PD metrics (EAD/LGD)
 * - Assembly of full risk metrics combining PD with pre-computed values
 * - Calculation of ECL and risk grade
 *
 * This service enables reuse of complex orchestration logic without code
 * duplication or breaking clean architecture principles.
 *
 * Usage:
 * - Scoring: Use to calculate metrics for a new scoring request
 * - Simulation: Use to calculate metrics for a what-if scenario
 * - Any other use case: Use to calculate risk metrics with custom payload
 *
 * @author Lucia Fernandez Mancebo
 * @Date 09-05-2026
 */
@Slf4j
@Component
public class RiskMetricsCalculationService {

        private final ModelPredictionPort modelPredictionPort;
        private final List<RiskCalculationStrategy> riskCalculationStrategies;
        private final RiskGradeCalculator riskGradeCalculator;
        private final FinancialMetricsCalculationService financialMetricsCalculationService;

        /**
         * Constructor of the RiskMetricsCalculationService class.
         *
         * @param modelPredictionPort                the output port for model
         *                                           invocation.
         * @param riskCalculationStrategies          the available risk calculation
         *                                           strategies.
         * @param riskGradeCalculator                the domain service for risk grade
         *                                           calculation.
         * @param financialMetricsCalculationService the domain service for financial
         *                                           metrics calculation.
         */
        public RiskMetricsCalculationService(
                        final ModelPredictionPort modelPredictionPort,
                        final List<RiskCalculationStrategy> riskCalculationStrategies,
                        final RiskGradeCalculator riskGradeCalculator,
                        final FinancialMetricsCalculationService financialMetricsCalculationService) {
                this.modelPredictionPort = Objects.requireNonNull(modelPredictionPort,
                                LogMessage.MODEL_PREDICTION_PORT_CANNOT_BE_NULL);
                this.riskCalculationStrategies = Objects.requireNonNull(riskCalculationStrategies,
                                LogMessage.STRATEGIES_LIST_CANNOT_BE_NULL);
                this.riskGradeCalculator = Objects.requireNonNull(riskGradeCalculator,
                                LogMessage.RISK_GRADE_CALCULATOR_CANNOT_BE_NULL);
                this.financialMetricsCalculationService = Objects.requireNonNull(financialMetricsCalculationService,
                                LogMessage.FINANCIAL_METRICS_CALCULATION_SERVICE_CANNOT_BE_NULL);
        }

        /**
         * Calculates full risk metrics with model invocation and metric assembly.
         *
         * Execution flow:
         * 1. Fire async model invocation (returns immediately)
         * 2. While model processes, pre-compute EAD/LGD in parallel
         * 3. Join futures when both complete
         * 4. Assemble full metrics (ECL, RiskGrade) combining PD with pre-computed
         * values
         *
         * This orchestration pattern minimizes latency by parallelizing independent
         * computations.
         *
         * @param context the RiskMetricsCalculationContext containing all necessary
         *                data for calculation.
         * @return the calculation result containing both model prediction and fully
         *         assembled RiskMetrics entity.
         */
        public RiskMetricsCalculationResult calculateRiskMetrics(final RiskMetricsCalculationContext context) {
                Objects.requireNonNull(context, LogMessage.RISK_METRICS_CONTEXT_CANNOT_BE_NULL);

                Map<String, Object> modelPayload = context.modelPayload();

                log.info(LogMessage.RISK_METRICS_CALCULATION_STARTED, context.requestId());

                // Step 1: Fire async model call (returns immediately)
                final CompletableFuture<ModelPredictionResult> modelFuture = modelPredictionPort.predictAsync(
                                context.modelPayload(),
                                context.requestId(),
                                context.modelEndpointPath());

                log.debug(LogMessage.ASYNCHRONOUS_MODEL_INVOCATION, context.requestId());

                Double amount = (Double) modelPayload.get(ModelPayloadFieldNames.FIELD_LOAN_AMOUNT);
                if (amount == null) {
                        amount = (Double) modelPayload.get(ModelPayloadFieldNames.FIELD_CREDIT_LIMIT);
                }
                
                Boolean isRevolving = (Boolean) modelPayload.get(ModelPayloadFieldNames.FIELD_IS_REVOLVING);

                // Step 2: While model processes, pre-compute EAD/LGD in parallel
                final RiskCalculationStrategy riskStrategy = RiskCalculationStrategyFactory.createStrategy(
                                context.requestType(),
                                isRevolving,
                                riskCalculationStrategies);

                final RiskMetrics prePdMetrics = riskStrategy.calculatePrePdMetrics(
                                amount,
                                (Double) modelPayload.get(ModelPayloadFieldNames.FIELD_LTV));

                log.debug(LogMessage.PRE_PD_METRICS_COMPUTED,
                                context.requestId(),
                                prePdMetrics.getProbabilityOfDefault(),
                                prePdMetrics.getLossGivenDefault());

                // Step 3: Join futures and get model result
                final ModelPredictionResult prediction = modelFuture.join();
                log.info(LogMessage.MODEL_PREDICTION_RESULT,
                                context.requestId(),
                                prediction.getProbabilityOfDefault());

                // Step 4: Assemble full metrics combining PD with pre-computed EAD/LGD
                final RiskMetrics fullMetrics = riskStrategy.assembleFullMetricsWithGradeCalculator(
                                prediction.getProbabilityOfDefault(),
                                prePdMetrics,
                                amount,
                                (Double) modelPayload.get(ModelPayloadFieldNames.FIELD_ANNUAL_INCOME),
                                (Integer) modelPayload.get(ModelPayloadFieldNames.FIELD_TERM_MONTHS),
                                (Double) modelPayload.get(ModelPayloadFieldNames.FIELD_INTEREST_RATE),
                                riskGradeCalculator);

                log.info(LogMessage.FULL_METRICS_ASSEMBLED,
                                context.requestId(),
                                fullMetrics.getProbabilityOfDefault(),
                                fullMetrics.getExposureAtDefault(),
                                fullMetrics.getLossGivenDefault(),
                                fullMetrics.getExpectedCalculatedLoss(),
                                fullMetrics.getRiskLevel());

                // Step 5: Calculate and attach financial metrics
                final FinancialMetrics financialMetrics = financialMetricsCalculationService.calculateFinancialMetrics(
                                context.requestType(),
                                isRevolving,
                                amount,
                                (Double) modelPayload.get(ModelPayloadFieldNames.FIELD_INTEREST_RATE),
                                (Integer) modelPayload.get(ModelPayloadFieldNames.FIELD_TERM_MONTHS),
                                (Double) modelPayload.get(ModelPayloadFieldNames.FIELD_ANNUAL_INCOME),
                                0.0);

                fullMetrics.setFinancialMetrics(financialMetrics);
                log.debug(LogMessage.FINANCIAL_METRICS_ATTACHED,
                                financialMetrics.getMonthlyPayment(),
                                financialMetrics.getDebtToIncomeRatio());

                return new RiskMetricsCalculationResult(prediction, fullMetrics);
        }
}
