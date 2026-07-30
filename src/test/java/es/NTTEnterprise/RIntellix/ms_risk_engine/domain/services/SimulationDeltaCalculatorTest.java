package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.FinancialMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.FinancialMetricsStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;

/**
 * Unit tests for {@link SimulationDeltaCalculator}.
 * Covers delta calculation logic for standard and credit card cases.
 */
@DisplayName("SimulationDeltaCalculator Tests")
@ExtendWith(MockitoExtension.class)
class SimulationDeltaCalculatorTest {

    @Mock
    private FinancialMetricsStrategy strategy;

    private SimulationDeltaCalculator calculator;

    @BeforeEach
    void setUp() {
        lenient().when(strategy.supports(anyString(), anyBoolean())).thenReturn(true);
        calculator = new SimulationDeltaCalculator(List.of(strategy));
    }

    @Test
    @DisplayName("Should calculate correct delta for standard loan")
    void calculateDelta_standardLoan() {
        // Base scoring setup
        Scoring baseScoring = new Scoring();
        RiskMetrics baseMetrics = new RiskMetrics();
        baseMetrics.setProbabilityOfDefault(0.05);
        baseMetrics.setExpectedCalculatedLoss(100.0);
        baseMetrics.setRiskLevel("C");
        
        FinancialMetrics baseFinMetrics = new FinancialMetrics();
        baseFinMetrics.setMonthlyPayment(300.0);
        baseFinMetrics.setDebtToIncomeRatio(0.30);
        baseFinMetrics.setTotalPayment(10800.0);
        baseFinMetrics.setTotalInterest(800.0);
        baseFinMetrics.setMonthlyDisposableIncome(2000.0);
        baseMetrics.setFinancialMetrics(baseFinMetrics);
        baseScoring.setResults(baseMetrics);

        // Simulated metrics setup
        RiskMetrics simMetrics = new RiskMetrics();
        simMetrics.setProbabilityOfDefault(0.08);
        simMetrics.setExpectedCalculatedLoss(150.0);
        simMetrics.setRiskLevel("D");

        // Strategy mock response
        FinancialMetrics simFinMetrics = new FinancialMetrics();
        simFinMetrics.setMonthlyPayment(400.0);
        simFinMetrics.setTotalPayment(14400.0);
        simFinMetrics.setTotalInterest(1400.0);
        simFinMetrics.setMonthlyDisposableIncome(1900.0);
        
        when(strategy.calculateFinancialMetrics(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(simFinMetrics);

        // Input maps
        Map<String, Object> baseVariables = new HashMap<>();
        Map<String, Object> mergedVariables = new HashMap<>();
        mergedVariables.put(ModelPayloadFieldNames.FIELD_REQUEST_TYPE, "PRESTAMO");
        mergedVariables.put(ModelPayloadFieldNames.FIELD_LOAN_AMOUNT, 12000.0);
        mergedVariables.put(ModelPayloadFieldNames.FIELD_DTI, 0.40); // New DTI is 0.40

        // Execute
        SimulationDelta delta = calculator.calculateDelta(baseScoring, simMetrics, baseVariables, mergedVariables);

        // Verify
        assertEquals(0.03, delta.getPdChange(), 0.001); // 0.08 - 0.05
        assertEquals(50.0, delta.getEclChange(), 0.001); // 150 - 100
        assertEquals("C -> D", delta.getRiskGradeChange());
        assertEquals(100.0, delta.getMonthlyPaymentChange(), 0.001); // 400 - 300
        assertEquals(0.10, delta.getDtiChange(), 0.001); // 0.40 - 0.30
        assertEquals(3600.0, delta.getTotalPaymentChange(), 0.001); // 14400 - 10800
        assertEquals(600.0, delta.getTotalInterestChange(), 0.001); // 1400 - 800
        assertEquals(100.0, delta.getMonthlyDisposableIncomeChange(), 0.001); // 2000 - 1900
    }

    @Test
    @DisplayName("Constructor should throw NPE when strategies list is null")
    void constructor_shouldThrow_whenStrategiesNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, 
            () -> new SimulationDeltaCalculator(null));
        assertEquals(LogMessage.FINANCIAL_METRICS_STRATEGIES_CANNOT_BE_NULL, exception.getMessage());
    }

    @Test
    @DisplayName("Should handle missing base financial metrics gracefully")
    void calculateDelta_nullBaseFinancialMetrics() {
        Scoring baseScoring = new Scoring();
        RiskMetrics baseMetrics = new RiskMetrics();
        baseMetrics.setProbabilityOfDefault(0.05);
        baseScoring.setResults(baseMetrics);

        RiskMetrics simMetrics = new RiskMetrics();
        simMetrics.setProbabilityOfDefault(0.08);
        
        FinancialMetrics simFinMetrics = new FinancialMetrics();
        simFinMetrics.setMonthlyPayment(400.0);
        simFinMetrics.setTotalPayment(14400.0);
        simFinMetrics.setTotalInterest(0.0);
        simFinMetrics.setMonthlyDisposableIncome(0.0);
        simFinMetrics.setDebtToIncomeRatio(0.0);
        
        when(strategy.calculateFinancialMetrics(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(simFinMetrics);

        Map<String, Object> baseVariables = new HashMap<>();
        Map<String, Object> mergedVariables = new HashMap<>();
        mergedVariables.put(ModelPayloadFieldNames.FIELD_REQUEST_TYPE, "PRESTAMO");
        mergedVariables.put(ModelPayloadFieldNames.FIELD_DTI, 0.40); 

        SimulationDelta delta = calculator.calculateDelta(baseScoring, simMetrics, baseVariables, mergedVariables);

        // Since base had no payment, delta should be current payment
        assertEquals(400.0, delta.getMonthlyPaymentChange(), 0.001);
        assertEquals(14400.0, delta.getTotalPaymentChange(), 0.001);
    }

    @Test
    @DisplayName("Should handle zero change correctly")
    void calculateDelta_zeroChange() {
        Scoring baseScoring = new Scoring();
        RiskMetrics baseMetrics = new RiskMetrics();
        baseMetrics.setProbabilityOfDefault(0.05);
        baseMetrics.setRiskLevel("C");
        baseScoring.setResults(baseMetrics);

        RiskMetrics simMetrics = new RiskMetrics();
        simMetrics.setProbabilityOfDefault(0.05);
        simMetrics.setRiskLevel("C");
        
        FinancialMetrics simFinMetrics = new FinancialMetrics();
        simFinMetrics.setMonthlyPayment(0.0);
        simFinMetrics.setTotalPayment(0.0);
        simFinMetrics.setTotalInterest(0.0);
        simFinMetrics.setMonthlyDisposableIncome(0.0);
        simFinMetrics.setDebtToIncomeRatio(0.0);
        
        when(strategy.calculateFinancialMetrics(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn(simFinMetrics);

        SimulationDelta delta = calculator.calculateDelta(baseScoring, simMetrics, new HashMap<>(), new HashMap<>());

        assertEquals(0.0, delta.getPdChange(), 0.001);
        assertEquals("C -> C", delta.getRiskGradeChange());
    }
}
