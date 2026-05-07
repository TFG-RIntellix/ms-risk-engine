package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.ContractCategory;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RiskGrade;

@DisplayName("RiskCalculationService")
class RiskCalculationServiceTest {

    private final RiskCalculationService service = new RiskCalculationService(
            0.50,
            0.75,
            0.70,
            0.80,
            0.90,
            0.20,
            0.10,
            0.10);

    @Test
    @DisplayName("Given mortgage contract when calculate then apply mortgage-specific EAD and LGD")
    void givenMortgage_whenCalculate_thenApplyMortgageRules() {
        RiskMetrics result = service.calculate(
                0.10,
                ContractCategory.MORTGAGE,
                100000.0,
                0.80,
                42000.0,
                240,
                4.0);

        assertThat(result.getEad()).isEqualTo(100000.0);
        assertThat(result.getLgd()).isGreaterThanOrEqualTo(0.10);
        assertThat(result.getEcl()).isPositive();
        assertThat(result.getRiskGrade()).isIn(RiskGrade.A, RiskGrade.B, RiskGrade.C, RiskGrade.D);
    }

    @Test
    @DisplayName("Given revolving card when calculate then use revolving CCF and LGD")
    void givenRevolvingCard_whenCalculate_thenUseRevolvingFactors() {
        RiskMetrics result = service.calculate(
                0.20,
                ContractCategory.CC_REVOLVING,
                10000.0,
                0.0,
                25000.0,
                24,
                18.0);

        assertThat(result.getEad()).isEqualTo(7500.0);
        assertThat(result.getLgd()).isEqualTo(0.90);
        assertThat(result.getEcl()).isEqualTo(0.20 * 0.90 * 7500.0);
    }
}
