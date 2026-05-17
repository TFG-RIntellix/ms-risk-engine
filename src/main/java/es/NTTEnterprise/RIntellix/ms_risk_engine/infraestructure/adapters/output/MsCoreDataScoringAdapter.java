package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output;

import java.util.Map;
import java.util.Objects;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringDTO;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.mappers.ScoringMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.FetchScoringPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output.clients.MsCoreDataClient;

@Component
public class MsCoreDataScoringAdapter implements FetchScoringPort {

    private final MsCoreDataClient msCoreDataClient;
    private final ScoringMapper scoringMapper;

    public MsCoreDataScoringAdapter(final MsCoreDataClient msCoreDataClient, final ScoringMapper scoringMapper) {
        this.msCoreDataClient = Objects.requireNonNull(msCoreDataClient);
        this.scoringMapper = Objects.requireNonNull(scoringMapper);
    }

    @Override
    public Scoring fetchByRequestId(final String requestId) {

        // Call ms-core-data to retrieve the scoring data for the given requestId

        final ResponseEntity<ScoringDTO> response = msCoreDataClient.getScoringByRequestId(requestId);
        // If not successfull then we raise an error
        if (response.getStatusCode().isError()) {
            throw new ScoringNotFoundException(LogMessage.SCORING_RETRIEVING_MESSAGE_ERROR + requestId + response.getStatusCode() + response.getBody());
        }

        final ScoringDTO retrievedScoringDTO = response.getBody();

        // Map
        return scoringMapper.toDomain(retrievedScoringDTO);

    }

}
