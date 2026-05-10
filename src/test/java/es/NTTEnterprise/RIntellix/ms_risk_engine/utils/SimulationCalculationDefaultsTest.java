package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SimulationCalculationDefaults")
class SimulationCalculationDefaultsTest {

    private static final double DELTA = 0.01;

    @Test
    @DisplayName("Given simulation values when calculating metrics then formulas are applied")
    void givenSimulationValues_whenCalculatingMetrics_thenFormulasAreApplied() {
        final double monthlyPayment = SimulationCalculationDefaults.calculateMonthlyPayment(150000.0, 3.5, 240);
        final double dti = SimulationCalculationDefaults.calculateDti(monthlyPayment, 45000.0);
        final double totalPayment = SimulationCalculationDefaults.calculateTotalPayment(monthlyPayment, 240);
        final double totalInterest = SimulationCalculationDefaults.calculateTotalInterest(totalPayment, 150000.0);
        final double disposableIncome = SimulationCalculationDefaults.calculateDisposableIncome(45000.0, monthlyPayment);

        assertThat(monthlyPayment).isCloseTo(869.94, org.assertj.core.data.Offset.offset(DELTA));
        assertThat(dti).isCloseTo((monthlyPayment / (45000.0 / 12.0)) * 100.0, org.assertj.core.data.Offset.offset(DELTA));
        assertThat(totalPayment).isCloseTo(monthlyPayment * 240.0, org.assertj.core.data.Offset.offset(DELTA));
        assertThat(totalInterest).isCloseTo(totalPayment - 150000.0, org.assertj.core.data.Offset.offset(DELTA));
        assertThat(disposableIncome).isCloseTo((45000.0 / 12.0) - monthlyPayment, org.assertj.core.data.Offset.offset(DELTA));
    }
}
