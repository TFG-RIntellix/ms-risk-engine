package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FinancialMetricsCalculator")
class FinancialMetricsCalculatorTest {

    @Test
    @DisplayName("Given sample loan data when calculating metrics then formulas are applied")
    void givenSampleLoanData_whenCalculating_thenApplyExpectedFormulas() {
        double monthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(150000, 3.5, 240);
        double dti = FinancialMetricsCalculator.calculateDti(monthlyPayment, 45000);
        double totalPayment = FinancialMetricsCalculator.calculateTotalPayment(monthlyPayment, 240);
        double totalInterest = FinancialMetricsCalculator.calculateTotalInterest(totalPayment, 150000);
        double disposableIncome = FinancialMetricsCalculator.calculateDisposableIncome(45000, monthlyPayment);

        assertThat(monthlyPayment).isGreaterThan(0.0);
        assertThat(dti).isGreaterThan(0.0);
        assertThat(totalPayment).isGreaterThan(150000);
        assertThat(totalInterest).isGreaterThan(0.0);
        assertThat(disposableIncome).isLessThan(45000.0 / 12.0);
    }
}
