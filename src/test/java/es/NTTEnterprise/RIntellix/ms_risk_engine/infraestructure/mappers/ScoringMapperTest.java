package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.common.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.NamingConverter;

class ScoringMapperTest {

    @Test
    @DisplayName("Normalizes snake_case input snapshot keys and maps aliases correctly to camelCase")
    void shouldNormalizeInputSnapshotKeysToCamelCase() {
        ScoringMapper mapper = new ScoringMapper(new NamingConverter());

        Map<String, Object> inputFeatures = new HashMap<>();
        inputFeatures.put("annual_income", 120000.0);
        inputFeatures.put("existing_obligations", 4000.0);
        inputFeatures.put("requested_amount", 50000.0);
        inputFeatures.put("interest_rate", 0.05);
        inputFeatures.put("term_months", 36);
        inputFeatures.put("work_sector", "Tecnologia");
        inputFeatures.put("nr_dependants", 2);
        inputFeatures.put("request_type", "PRESTAMO");

        ScoringDTO dto = new ScoringDTO();
        dto.setScoringId("scoring-123");
        dto.setRequestId("req-123");
        dto.setInputFeatures(inputFeatures);

        Scoring scoring = mapper.toDomain(dto);

        assertThat(scoring).isNotNull();
        Map<String, Object> snapshot = scoring.getInputSnapshot();
        assertThat(snapshot).isNotNull();

        // Verify snake_case to camelCase conversion
        assertThat(snapshot.get(ModelPayloadFieldNames.FIELD_ANNUAL_INCOME)).isEqualTo(120000.0);
        assertThat(snapshot.get(ModelPayloadFieldNames.FIELD_EXISTING_OBLIGATIONS)).isEqualTo(4000.0);
        assertThat(snapshot.get(ModelPayloadFieldNames.FIELD_INTEREST_RATE)).isEqualTo(0.05);
        assertThat(snapshot.get(ModelPayloadFieldNames.FIELD_TERM_MONTHS)).isEqualTo(36);

        // Verify specific alias translations
        assertThat(snapshot.get(ModelPayloadFieldNames.FIELD_LOAN_AMOUNT)).isEqualTo(50000.0);
        assertThat(snapshot.get(ModelPayloadFieldNames.FIELD_OCCUPATION_SECTOR)).isEqualTo("Tecnologia");
        assertThat(snapshot.get(ModelPayloadFieldNames.FIELD_DEPENDENTS)).isEqualTo(2);
        assertThat(snapshot.get(ModelPayloadFieldNames.FIELD_LOAN_TYPE)).isEqualTo("PRESTAMO");

        // Verify original snake_case/alias keys are no longer present
        assertThat(snapshot).doesNotContainKey("annual_income");
        assertThat(snapshot).doesNotContainKey("existing_obligations");
        assertThat(snapshot).doesNotContainKey("requested_amount");
        assertThat(snapshot).doesNotContainKey("requestedAmount");
        assertThat(snapshot).doesNotContainKey("work_sector");
        assertThat(snapshot).doesNotContainKey("workSector");
        assertThat(snapshot).doesNotContainKey("nr_dependants");
        assertThat(snapshot).doesNotContainKey("nrDependants");
        assertThat(snapshot).doesNotContainKey("request_type");
        assertThat(snapshot).doesNotContainKey("requestType");
    }
}
