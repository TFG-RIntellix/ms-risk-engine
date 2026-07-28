package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link NamingConverter}.
 * Covers snake_case→camelCase, UPPERCASE→lowercase, PascalCase→camelCase, and edge cases.
 */
@DisplayName("NamingConverter Tests")
class NamingConverterTest {

    private NamingConverter converter;

    @BeforeEach
    void setUp() {
        converter = new NamingConverter();
    }

    @Test
    @DisplayName("Should convert snake_case to camelCase")
    void shouldConvert_snakeCase_toCamelCase() {
        assertEquals("loanAmount", converter.toCamelCase("loan_amount"));
    }

    @Test
    @DisplayName("Should convert UPPERCASE to lowercase")
    void shouldConvert_UPPERCASE_toLowercase() {
        assertEquals("field", converter.toCamelCase("FIELD"));
    }

    @Test
    @DisplayName("Should convert PascalCase to camelCase")
    void shouldConvert_PascalCase_toCamelCase() {
        assertEquals("loanAmount", converter.toCamelCase("LoanAmount"));
    }

    @Test
    @DisplayName("Should return null when input is null")
    void shouldReturnNull_whenNull() {
        assertNull(converter.toCamelCase(null));
    }

    @Test
    @DisplayName("Should return blank when input is blank")
    void shouldReturnBlank_whenBlank() {
        // blank input is returned as-is (isBlank check)
        assertEquals("  ", converter.toCamelCase("  "));
    }

    @Test
    @DisplayName("Should handle multiple consecutive underscores")
    void shouldHandle_multipleUnderscores() {
        assertEquals("loanAmount", converter.toCamelCase("loan__amount"));
    }

    @Test
    @DisplayName("Should handle single lowercase word without changes")
    void shouldHandle_singleWordLowercase() {
        assertEquals("amount", converter.toCamelCase("amount"));
    }

    @Test
    @DisplayName("Should handle three-part snake_case")
    void shouldHandle_threePartSnakeCase() {
        assertEquals("loanTermMonths", converter.toCamelCase("loan_term_months"));
    }

    @Test
    @DisplayName("Should handle UPPER_SNAKE_CASE")
    void shouldHandle_upperSnakeCase() {
        assertEquals("loanAmount", converter.toCamelCase("LOAN_AMOUNT"));
    }
}
