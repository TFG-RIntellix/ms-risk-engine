package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadUtilities;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.NamingConverter;

/**
 * Unit tests for {@link SimulationModelPayloadMapper}.
 * Covers normalization of base variables and form changes, delegating to Mocked dependencies.
 */
@DisplayName("SimulationModelPayloadMapper Tests")
@ExtendWith(MockitoExtension.class)
class SimulationModelPayloadMapperTest {

    @Mock
    private ModelPayloadUtilities payloadUtilities;

    @Mock
    private NamingConverter namingConverter;

    private SimulationModelPayloadMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SimulationModelPayloadMapper(payloadUtilities, namingConverter);
    }

    @Test
    @DisplayName("normalizeBaseVariables should process all entries")
    void normalizeBaseVariables_processesAll() {
        Map<String, Object> base = Map.of("loan_amount", 10000.0, "interest_rate", 5.0);
        
        when(namingConverter.toCamelCase("loan_amount")).thenReturn("loanAmount");
        when(namingConverter.toCamelCase("interest_rate")).thenReturn("interestRate");
        when(payloadUtilities.normalizeInterestRateToFraction(0.05)).thenReturn(0.05);

        Map<String, Object> result = mapper.normalizeBaseVariables(base);

        assertEquals(10000.0, result.get("loanAmount"));
        assertEquals(0.05, result.get("interestRate"));
        
        verify(namingConverter).toCamelCase("loan_amount");
        verify(payloadUtilities).normalizeInterestRateToFraction(0.05);
    }

    @Test
    @DisplayName("normalizeFormChangesToCamelcase should handle empty map")
    void normalizeFormChangesToCamelcase_emptyMap() {
        Map<String, Object> result = mapper.normalizeFormChangesToCamelcase(new HashMap<>());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("normalizeValue should convert booleans")
    void normalizeValue_booleans() {
        Map<String, Object> changes = Map.of("hasMortgage", true);
        
        when(namingConverter.toCamelCase("hasMortgage")).thenReturn("hasMortgage");
        when(payloadUtilities.toModelBoolean(true)).thenReturn("Si");

        Map<String, Object> result = mapper.normalizeFormChangesToCamelcase(changes);

        assertEquals("Si", result.get("hasMortgage"));
    }

    @Test
    @DisplayName("normalizeValue should normalize enums")
    void normalizeValue_enums() {
        Map<String, Object> changes = Map.of("gender", "HOMBRE");
        
        when(namingConverter.toCamelCase("gender")).thenReturn("gender");
        when(payloadUtilities.normalizeEnumForField("gender", "HOMBRE")).thenReturn("Hombre");

        Map<String, Object> result = mapper.normalizeFormChangesToCamelcase(changes);

        assertEquals("Hombre", result.get("gender"));
    }

    @Test
    @DisplayName("normalizeBaseVariables should throw on null")
    void normalizeBaseVariables_null() {
        assertThrows(NullPointerException.class, () -> mapper.normalizeBaseVariables(null));
    }
}
