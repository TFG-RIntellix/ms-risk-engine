package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.DtiCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.BooleanConverter;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.EnumNormalizer;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.FinancialMetricsCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.MathUtilities;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadUtilities;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;

class LoanOrMortgageModelPayloadMapperTest {

    @Test
    @DisplayName("Calculates model DTI using existing obligations and new payment")
    void shouldCalculateModelDtiWithExistingObligations() {
        ModelPayloadUtilities utilities = new ModelPayloadUtilities(new EnumNormalizer(), new BooleanConverter());
        LoanOrMortgageModelPayloadMapper mapper = new LoanOrMortgageModelPayloadMapper(
                utilities,
                new DtiCalculationService());

        ScoringGenerationRequest request = new ScoringGenerationRequest();
        request.setAnnualIncome(24000.0);
        request.setLoanAmount(12000.0);
        request.setInterestRate(6.0);
        request.setTermMonths(24);
        request.setDti(0.25);

        Map<String, Object> payload = mapper.toModelPayload(request);

        double monthlyIncome = request.getAnnualIncome() / SimulationConstants.MONTHS_PER_YEAR;
        double monthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(
                request.getLoanAmount(), request.getInterestRate(), request.getTermMonths());
        double expectedDti = MathUtilities.roundFinal((request.getDti() * monthlyIncome + monthlyPayment)
                / monthlyIncome);

        assertThat(payload.get(ModelPayloadFieldNames.FIELD_DTI)).isEqualTo(expectedDti);
    }

    @Test
    @DisplayName("Defaults DTI to zero when income is missing")
    void shouldDefaultDtiToZeroWhenIncomeMissing() {
        ModelPayloadUtilities utilities = new ModelPayloadUtilities(new EnumNormalizer(), new BooleanConverter());
        LoanOrMortgageModelPayloadMapper mapper = new LoanOrMortgageModelPayloadMapper(
                utilities,
                new DtiCalculationService());

        ScoringGenerationRequest request = new ScoringGenerationRequest();
        request.setLoanAmount(15000.0);
        request.setInterestRate(5.0);
        request.setTermMonths(36);
        request.setDti(0.3);

        Map<String, Object> payload = mapper.toModelPayload(request);

        assertThat(payload.get(ModelPayloadFieldNames.FIELD_DTI)).isEqualTo(SimulationConstants.ZERO_VALUE);
    }
}
