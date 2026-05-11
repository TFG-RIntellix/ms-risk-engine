package es.NTTEnterprise.RIntellix.ms_risk_engine.application.constraints;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ValidSimulationFormChangesValidator")
class ValidSimulationFormChangesValidatorTest {

    private final ValidSimulationFormChangesValidator validator = new ValidSimulationFormChangesValidator();

    @Test
    @DisplayName("Given allowed keys when validating then return true")
    void givenAllowedKeys_whenValidating_thenReturnTrue() {
        Map<String, Object> input = Map.of(
                "interest_rate", 4.5,
                "term_months", 120,
                "annual_income", 45000.0,
                "requested_credit_limit", 1500.0);

        boolean result = validator.isValid(input, null);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Given unsupported keys when validating then return false")
    void givenUnsupportedKeys_whenValidating_thenReturnFalse() {
        Map<String, Object> input = Map.of(
                "foo", 10,
                "interestRate", 6.0);

        boolean result = validator.isValid(input, null);

        assertThat(result).isFalse();
    }
}
