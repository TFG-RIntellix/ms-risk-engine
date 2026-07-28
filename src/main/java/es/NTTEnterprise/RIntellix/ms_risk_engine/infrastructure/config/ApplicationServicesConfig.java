package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.CreditCardModelPayloadMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.LoanOrMortgageModelPayloadMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.ScoringResultMessageDTOMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.ScoringResultMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.SimulationModelPayloadMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.input.SimulationDraftPortService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.input.ScoringProcessingPortService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies.CreditCardScoringModelExecutionStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies.LoanOrMortgageScoringModelExecutionStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.strategies.ScoringModelExecutionStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases.CalculateSimulationDraftUseCase;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases.RiskMetricsCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases.ScoringProcessingService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.FetchScoringPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.ModelPredictionPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.ScoringResultPublisherPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.FinancialMetricsCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.HardCutoffRuleEvaluator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskGradeCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskIndicatorCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.SimulationDeltaCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.RiskCalculationStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.FinancialMetricsStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.LoanFinancialMetricsStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.StandardCreditCardFinancialMetricsStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.RevolvingCreditCardFinancialMetricsStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadUtilities;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.DtiCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.NamingConverter;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.EnumNormalizer;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.BooleanConverter;

/**
 * Configuration class that instantiates all application layer services as Spring Beans.
 * This keeps the application layer completely independent of the Spring framework
 * in accordance with Hexagonal Architecture.
 *
 * @author Lucía Fernández Mancebo
 * @Date 07-28-2026
 */
@Configuration
public class ApplicationServicesConfig {

    // --- Utilities & Domain Services ---

    @Bean
    public EnumNormalizer enumNormalizer() {
        return new EnumNormalizer();
    }

    @Bean
    public BooleanConverter booleanConverter() {
        return new BooleanConverter();
    }
    
    @Bean
    public ModelPayloadUtilities modelPayloadUtilities(EnumNormalizer enumNormalizer, BooleanConverter booleanConverter) {
        return new ModelPayloadUtilities(enumNormalizer, booleanConverter);
    }

    @Bean
    public NamingConverter namingConverter() {
        return new NamingConverter();
    }

    @Bean
    public DtiCalculationService dtiCalculationService() {
        return new DtiCalculationService();
    }

    @Bean
    public FinancialMetricsStrategy loanFinancialMetricsStrategy() {
        return new LoanFinancialMetricsStrategy();
    }

    @Bean
    public FinancialMetricsStrategy standardCreditCardFinancialMetricsStrategy() {
        return new StandardCreditCardFinancialMetricsStrategy();
    }

    @Bean
    public FinancialMetricsStrategy revolvingCreditCardFinancialMetricsStrategy() {
        return new RevolvingCreditCardFinancialMetricsStrategy();
    }

    @Bean
    public FinancialMetricsCalculationService financialMetricsCalculationService(
            List<FinancialMetricsStrategy> strategies) {
        return new FinancialMetricsCalculationService(strategies);
    }

    @Bean
    public HardCutoffRuleEvaluator hardCutoffRuleEvaluator() {
        return new HardCutoffRuleEvaluator();
    }

    @Bean
    public RiskGradeCalculator riskGradeCalculator() {
        return new RiskGradeCalculator();
    }

    @Bean
    public RiskIndicatorCalculationService riskIndicatorCalculationService(
            DtiCalculationService dtiCalculationService) {
        return new RiskIndicatorCalculationService(dtiCalculationService);
    }

    @Bean
    public SimulationDeltaCalculator simulationDeltaCalculator(
            List<FinancialMetricsStrategy> strategies) {
        return new SimulationDeltaCalculator(strategies);
    }

    // --- Mappers ---

    @Bean
    public CreditCardModelPayloadMapper creditCardModelPayloadMapper(
            ModelPayloadUtilities modelPayloadUtilities,
            DtiCalculationService dtiCalculationService) {
        return new CreditCardModelPayloadMapper(modelPayloadUtilities, dtiCalculationService);
    }

    @Bean
    public LoanOrMortgageModelPayloadMapper loanOrMortgageModelPayloadMapper(
            ModelPayloadUtilities modelPayloadUtilities,
            DtiCalculationService dtiCalculationService) {
        return new LoanOrMortgageModelPayloadMapper(modelPayloadUtilities, dtiCalculationService);
    }

    @Bean
    public ScoringResultMessageDTOMapper scoringResultMessageDTOMapper() {
        return new ScoringResultMessageDTOMapper();
    }

    @Bean
    public ScoringResultMapper scoringResultMapper() {
        return new ScoringResultMapper();
    }

    @Bean
    public SimulationModelPayloadMapper simulationModelPayloadMapper(
            ModelPayloadUtilities modelPayloadUtilities,
            NamingConverter namingConverter) {
        return new SimulationModelPayloadMapper(modelPayloadUtilities, namingConverter);
    }

    // --- Use Cases & Strategies ---

    @Bean
    public RiskMetricsCalculationService riskMetricsCalculationService(
            ModelPredictionPort modelPredictionPort,
            List<RiskCalculationStrategy> riskCalculationStrategies,
            RiskGradeCalculator riskGradeCalculator,
            FinancialMetricsCalculationService financialMetricsCalculationService,
            HardCutoffRuleEvaluator hardCutoffRuleEvaluator) {
        return new RiskMetricsCalculationService(modelPredictionPort, riskCalculationStrategies, riskGradeCalculator, financialMetricsCalculationService, hardCutoffRuleEvaluator);
    }

    @Bean
    public LoanOrMortgageScoringModelExecutionStrategy loanOrMortgageScoringModelExecutionStrategy(
            LoanOrMortgageModelPayloadMapper loanOrMortgageModelPayloadMapper,
            RiskMetricsCalculationService riskMetricsCalculationService,
            @Value("${risk.model.predict-loan-path:/api/v1/risk/predict-loan}") String predictLoanPath) {
        return new LoanOrMortgageScoringModelExecutionStrategy(loanOrMortgageModelPayloadMapper, riskMetricsCalculationService, predictLoanPath);
    }

    @Bean
    public CreditCardScoringModelExecutionStrategy creditCardScoringModelExecutionStrategy(
            CreditCardModelPayloadMapper creditCardModelPayloadMapper,
            RiskMetricsCalculationService riskMetricsCalculationService,
            @Value("${risk.model.predict-credit-card-path:/api/v1/risk/predict-credit-card}") String predictCreditCardPath) {
        return new CreditCardScoringModelExecutionStrategy(creditCardModelPayloadMapper, riskMetricsCalculationService, predictCreditCardPath);
    }

    @Bean
    public ScoringProcessingPortService scoringProcessingService(
            List<ScoringModelExecutionStrategy> scoringModelExecutionStrategies,
            ScoringResultMapper scoringResultMapper,
            ScoringResultPublisherPort scoringResultPublisherPort,
            @Value("${risk.model.version:XGBoost_v1.0}") String modelVersion) {
        return new ScoringProcessingService(scoringModelExecutionStrategies, scoringResultMapper, scoringResultPublisherPort, modelVersion);
    }

    @Bean
    public SimulationDraftPortService calculateSimulationDraftUseCase(
            FetchScoringPort fetchScoringPort,
            List<ScoringModelExecutionStrategy> scoringModelExecutionStrategies,
            RiskMetricsCalculationService riskMetricsCalculationService,
            RiskIndicatorCalculationService riskIndicatorCalculationService,
            SimulationModelPayloadMapper simulationModelPayloadMapper,
            SimulationDeltaCalculator simulationDeltaCalculator) {
        return new CalculateSimulationDraftUseCase(
                fetchScoringPort,
                scoringModelExecutionStrategies,
                riskMetricsCalculationService,
                riskIndicatorCalculationService,
                simulationModelPayloadMapper,
                simulationDeltaCalculator);
    }
}
