package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.FinancialMetricsCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.MathUtilities;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;

class RiskIndicatorCalculationServiceTest {

    @Test
    @DisplayName("Recalculates DTI using base obligations and new income")
    void shouldRecalculateDtiWithExistingObligationsAndIncomeChange() {
        RiskIndicatorCalculationService service = new RiskIndicatorCalculationService(new DtiCalculationService());

        Map<String, Object> baseSnapshot = new HashMap<>();
        baseSnapshot.put(ModelPayloadFieldNames.FIELD_ANNUAL_INCOME, 24000.0);
        baseSnapshot.put(ModelPayloadFieldNames.FIELD_LOAN_AMOUNT, 10000.0);
        baseSnapshot.put(ModelPayloadFieldNames.FIELD_INTEREST_RATE, 4.5);
        baseSnapshot.put(ModelPayloadFieldNames.FIELD_TERM_MONTHS, 24);
        baseSnapshot.put(ModelPayloadFieldNames.FIELD_DTI, 0.4);

        Map<String, Object> mergedVariables = new HashMap<>();
        mergedVariables.put(ModelPayloadFieldNames.FIELD_ANNUAL_INCOME, 36000.0);
        mergedVariables.put(ModelPayloadFieldNames.FIELD_LOAN_AMOUNT, 20000.0);
        mergedVariables.put(ModelPayloadFieldNames.FIELD_INTEREST_RATE, 6.0);
        mergedVariables.put(ModelPayloadFieldNames.FIELD_TERM_MONTHS, 36);

        service.recalculateRiskIndicators(mergedVariables, "PRESTAMO", baseSnapshot);

        double baseMonthlyIncome = 24000.0 / SimulationConstants.MONTHS_PER_YEAR;
        double baseMonthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(10000.0, 4.5, 24);
        double baseTotalMonthlyObligations = 0.4 * baseMonthlyIncome;
        double existingObligations = Math.max(baseTotalMonthlyObligations - baseMonthlyPayment,
                SimulationConstants.ZERO_VALUE);

        double newMonthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(20000.0, 6.0, 36);
        double newMonthlyIncome = 36000.0 / SimulationConstants.MONTHS_PER_YEAR;
        double expectedDti = MathUtilities.roundFinal((existingObligations + newMonthlyPayment) / newMonthlyIncome);

        assertThat(mergedVariables.get(ModelPayloadFieldNames.FIELD_DTI)).isEqualTo(expectedDti);
    }
}
