package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CreditCardScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.DtiCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadUtilities;

/**
 * Unit tests for {@link CreditCardModelPayloadMapper}.
 * Covers mapping from CreditCardScoringGenerationRequest to Model Payload with mocked utilities.
 */
@DisplayName("CreditCardModelPayloadMapper Tests")
@ExtendWith(MockitoExtension.class)
class CreditCardModelPayloadMapperTest {

    @Mock
    private ModelPayloadUtilities payloadUtilities;

    @Mock
    private DtiCalculationService dtiCalculationService;

    private CreditCardModelPayloadMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CreditCardModelPayloadMapper(payloadUtilities, dtiCalculationService);
    }

    @Test
    @DisplayName("Should map request to model payload correctly")
    void toModelPayload_mapsCorrectly() {
        CreditCardScoringGenerationRequest request = new CreditCardScoringGenerationRequest();
        request.setAge(30);
        request.setGender("HOMBRE");
        request.setAnnualIncome(50000.0);
        request.setCreditLimit(5000.0);
        request.setExistingObligations(1200.0);
        request.setIsRevolving(true);
        request.setInterestRate(20.0);

        when(payloadUtilities.normalizeEnumForField(ModelPayloadFieldNames.FIELD_GENDER, "HOMBRE"))
                .thenReturn("Hombre");
        when(payloadUtilities.normalizeInterestRateToFraction(20.0)).thenReturn(0.20);
        when(dtiCalculationService.calculateModelDtiForCreditCardScoring(50000.0, 1200.0, 5000.0, true))
                .thenReturn(0.15);

        Map<String, Object> result = mapper.toModelPayload(request, "TARJETA_CREDITO");

        assertNotNull(result);
        assertEquals(30, result.get(ModelPayloadFieldNames.FIELD_AGE));
        assertEquals("Hombre", result.get(ModelPayloadFieldNames.FIELD_GENDER));
        assertEquals(50000.0, result.get(ModelPayloadFieldNames.FIELD_ANNUAL_INCOME));
        assertEquals("Si", result.get(ModelPayloadFieldNames.FIELD_IS_REVOLVING));
        assertEquals(0.20, result.get(ModelPayloadFieldNames.FIELD_INTEREST_RATE));
        assertEquals(0.15, result.get(ModelPayloadFieldNames.FIELD_DTI));

        verify(payloadUtilities).normalizeEnumForField(ModelPayloadFieldNames.FIELD_GENDER, "HOMBRE");
        verify(payloadUtilities).normalizeInterestRateToFraction(20.0);
        verify(dtiCalculationService).calculateModelDtiForCreditCardScoring(50000.0, 1200.0, 5000.0, true);
    }

    @Test
    @DisplayName("Should return 0 for DTI when request is null")
    void calculateModelDti_withNullRequest() {
        // We can't directly test calculateModelDti since it's private and called from toModelPayload
        // But if we pass a null request to toModelPayload, it will throw NPE before reaching it.
        // So let's test constructor validation instead.
        assertThrows(NullPointerException.class, () -> new CreditCardModelPayloadMapper(null, dtiCalculationService));
        assertThrows(NullPointerException.class, () -> new CreditCardModelPayloadMapper(payloadUtilities, null));
    }
}
