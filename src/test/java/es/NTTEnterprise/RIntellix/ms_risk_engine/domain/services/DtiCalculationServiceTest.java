package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;

/**
 * Unit tests for {@link DtiCalculationService}.
 * Covers DTI for scoring, credit card scoring, existing obligations, and monthly obligation resolution.
 */
@DisplayName("DtiCalculationService Tests")
class DtiCalculationServiceTest {

    private DtiCalculationService service;

    @BeforeEach
    void setUp() {
        service = new DtiCalculationService();
    }

    // ========== calculateModelDtiForScoring ==========

    @Test
    @DisplayName("Should calculate model DTI for standard scoring case")
    void calculateModelDtiForScoring_standardCase() {
        double result = service.calculateModelDtiForScoring(60000.0, 6000.0, 20000.0, 5.0, 36);
        assertTrue(result > 0, "DTI should be positive for standard inputs");
    }

    @Test
    @DisplayName("Should return zero DTI when annual income is zero")
    void calculateModelDtiForScoring_zeroIncome() {
        double result = service.calculateModelDtiForScoring(0.0, 0.0, 20000.0, 5.0, 36);
        assertEquals(0.0, result);
    }

    @Test
    @DisplayName("Should return zero DTI when annual income is negative")
    void calculateModelDtiForScoring_negativeIncome() {
        double result = service.calculateModelDtiForScoring(-10000.0, 0.0, 20000.0, 5.0, 36);
        assertEquals(0.0, result);
    }

    @Test
    @DisplayName("Should use MIN_TERM_MONTHS when termMonths is null")
    void calculateModelDtiForScoring_nullTermMonths() {
        double result = service.calculateModelDtiForScoring(60000.0, 0.0, 20000.0, 5.0, null);
        assertTrue(result > 0);
    }

    // ========== calculateModelDtiForCreditCardScoring ==========

    @Test
    @DisplayName("Should calculate model DTI for revolving credit card")
    void calculateModelDtiForCreditCardScoring_revolving() {
        double result = service.calculateModelDtiForCreditCardScoring(60000.0, 6000.0, 5000.0, true);
        assertTrue(result > 0);
    }

    @Test
    @DisplayName("Should calculate model DTI for standard credit card")
    void calculateModelDtiForCreditCardScoring_standard() {
        double result = service.calculateModelDtiForCreditCardScoring(60000.0, 6000.0, 5000.0, false);
        assertTrue(result > 0);
        // Standard CC payment = creditLimit = 5000, so DTI should be higher
    }

    @Test
    @DisplayName("Should return zero DTI for CC when income is zero")
    void calculateModelDtiForCreditCardScoring_zeroIncome() {
        double result = service.calculateModelDtiForCreditCardScoring(0.0, 0.0, 5000.0, true);
        assertEquals(0.0, result);
    }

    // ========== calculateDtiWithExistingObligations ==========

    @Test
    @DisplayName("Should calculate DTI with existing obligations standard case")
    void calculateDtiWithExistingObligations_standardCase() {
        // monthlyPayment=500, annualIncome=60000 (monthlyIncome=5000), existingObligations=200
        // DTI = (200+500)/5000 = 0.14
        double result = service.calculateDtiWithExistingObligations(500.0, 60000.0, 200.0);
        assertEquals(0.14, result, 0.001);
    }

    @Test
    @DisplayName("Should return zero DTI when monthly income is zero")
    void calculateDtiWithExistingObligations_zeroIncome() {
        double result = service.calculateDtiWithExistingObligations(500.0, 0.0, 200.0);
        assertEquals(0.0, result);
    }

    // ========== resolveExistingMonthlyObligations ==========

    @Test
    @DisplayName("Should return zero for null snapshot")
    void resolveExistingMonthlyObligations_nullSnapshot() {
        assertEquals(0.0, service.resolveExistingMonthlyObligations(null));
    }

    @Test
    @DisplayName("Should return zero for empty snapshot")
    void resolveExistingMonthlyObligations_emptySnapshot() {
        assertEquals(0.0, service.resolveExistingMonthlyObligations(new HashMap<>()));
    }

    @Test
    @DisplayName("Should resolve obligations for loan-based snapshot")
    void resolveExistingMonthlyObligations_loanBased() {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put(ModelPayloadFieldNames.FIELD_ANNUAL_INCOME, 60000.0);
        snapshot.put(ModelPayloadFieldNames.FIELD_LOAN_AMOUNT, 100000.0);
        snapshot.put(ModelPayloadFieldNames.FIELD_INTEREST_RATE, 5.0);
        snapshot.put(ModelPayloadFieldNames.FIELD_TERM_MONTHS, 36.0);
        snapshot.put(ModelPayloadFieldNames.FIELD_DTI, 0.30);

        double result = service.resolveExistingMonthlyObligations(snapshot);
        assertTrue(result >= 0, "Existing obligations should be non-negative");
    }

    @Test
    @DisplayName("Should resolve obligations for credit-card-based snapshot")
    void resolveExistingMonthlyObligations_creditCardBased() {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put(ModelPayloadFieldNames.FIELD_ANNUAL_INCOME, 60000.0);
        snapshot.put(ModelPayloadFieldNames.FIELD_CREDIT_LIMIT, 5000.0);
        snapshot.put(ModelPayloadFieldNames.FIELD_IS_REVOLVING, Boolean.TRUE);
        snapshot.put(ModelPayloadFieldNames.FIELD_DTI, 0.10);

        double result = service.resolveExistingMonthlyObligations(snapshot);
        assertTrue(result >= 0, "Existing obligations should be non-negative");
    }

    @Test
    @DisplayName("Should return zero when base annual income is zero")
    void resolveExistingMonthlyObligations_zeroBaseIncome() {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put(ModelPayloadFieldNames.FIELD_ANNUAL_INCOME, 0.0);
        snapshot.put(ModelPayloadFieldNames.FIELD_LOAN_AMOUNT, 100000.0);

        assertEquals(0.0, service.resolveExistingMonthlyObligations(snapshot));
    }

    @Test
    @DisplayName("Should never return negative obligations (clamped to zero)")
    void resolveExistingMonthlyObligations_neverNegative() {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put(ModelPayloadFieldNames.FIELD_ANNUAL_INCOME, 60000.0);
        snapshot.put(ModelPayloadFieldNames.FIELD_LOAN_AMOUNT, 1000.0);
        snapshot.put(ModelPayloadFieldNames.FIELD_INTEREST_RATE, 1.0);
        snapshot.put(ModelPayloadFieldNames.FIELD_TERM_MONTHS, 12.0);
        snapshot.put(ModelPayloadFieldNames.FIELD_DTI, 0.001); // very low DTI

        double result = service.resolveExistingMonthlyObligations(snapshot);
        assertTrue(result >= 0, "Result should never be negative due to Math.max clamp");
    }
}
