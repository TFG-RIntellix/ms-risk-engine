package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.LoanPaymentCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.FinancialMetricsCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.DtiCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskGradeCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.RiskIndicatorCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.SimulationDeltaCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.HardCutoffRuleEvaluator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.FinancialMetricsStrategy;

/**
 * Configuration class to wire domain services without Spring stereotypes
 * in the domain layer.
 *
 * @author Lucía Fernández Mancebo
 * @date 30/05/2026
 */
@Configuration
public class DomainServicesConfig {

    /**
     * Creates a FinancialMetricsCalculationService bean.
     *
     * @param strategies the list of financial metrics strategies
     * @return FinancialMetricsCalculationService instance
     */
    @Bean
    public FinancialMetricsCalculationService financialMetricsCalculationService(
            List<FinancialMetricsStrategy> strategies) {
        return new FinancialMetricsCalculationService(strategies);
    }

    /**
     * Creates a DtiCalculationService bean.
     *
     * @return DtiCalculationService instance
     */
    @Bean
    public DtiCalculationService dtiCalculationService() {
        return new DtiCalculationService();
    }

    /**
     * Creates a RiskIndicatorCalculationService bean.
     *
     * @return RiskIndicatorCalculationService instance
     */
    @Bean
    public RiskIndicatorCalculationService riskIndicatorCalculationService(
            final DtiCalculationService dtiCalculationService) {
        return new RiskIndicatorCalculationService(dtiCalculationService);
    }

    /**
     * Creates a RiskGradeCalculator bean.
     *
     * @return RiskGradeCalculator instance
     */
    @Bean
    public RiskGradeCalculator riskGradeCalculator() {
        return new RiskGradeCalculator();
    }

    /**
     * Creates a LoanPaymentCalculator bean.
     *
     * @return LoanPaymentCalculator instance
     */
    @Bean
    public LoanPaymentCalculator loanPaymentCalculator() {
        return new LoanPaymentCalculator();
    }

    /**
     * Creates a SimulationDeltaCalculator bean.
     *
     * @return SimulationDeltaCalculator instance
     */
    @Bean
    public SimulationDeltaCalculator simulationDeltaCalculator() {
        return new SimulationDeltaCalculator();
    }

    /**
     * Creates a HardCutoffRuleEvaluator bean.
     *
     * @return HardCutoffRuleEvaluator instance
     */
    @Bean
    public HardCutoffRuleEvaluator hardCutoffRuleEvaluator() {
        return new HardCutoffRuleEvaluator();
    }
}
