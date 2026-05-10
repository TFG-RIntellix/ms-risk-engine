package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Simulation;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.SimulationPersistenceException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.SimulationRepositoryPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output.dtos.SimulationExistsResponse;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output.dtos.SimulationPersistenceRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;

/**
 * Output adapter for simulation data integration with ms-core-data API.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
@Component
public class SimulationCoreDataAdapter implements SimulationRepositoryPort {

    private final WebClient webClient;
    private final String fetchScoringPath;
    private final String existsNamePath;
    private final String savePath;
    private final boolean extendedMetricsEnabled;

    /**
     * Constructor of the SimulationCoreDataAdapter class.
     *
     * @param baseUrl                core-data base URL.
     * @param fetchScoringPath       path to fetch base scoring by request id.
     * @param existsNamePath         path to validate scenario-name uniqueness.
     * @param savePath               path to persist simulation.
     * @param extendedMetricsEnabled schema flag for new simulation fields.
     */
    public SimulationCoreDataAdapter(
            @Value("${core-data.base-url:http://localhost:8081}") final String baseUrl,
            @Value("${core-data.simulation.fetch-scoring-path:/api/v1/scorings/request/{requestId}}") final String fetchScoringPath,
            @Value("${core-data.simulation.exists-name-path:/api/v1/simulations/exists}") final String existsNamePath,
            @Value("${core-data.simulation.save-path:/api/v1/simulations}") final String savePath,
            @Value("${core-data.simulation.extended-fields-enabled:false}") final boolean extendedMetricsEnabled) {
        this.webClient = WebClient.builder().baseUrl(Objects.requireNonNull(baseUrl)).build();
        this.fetchScoringPath = Objects.requireNonNull(fetchScoringPath);
        this.existsNamePath = Objects.requireNonNull(existsNamePath);
        this.savePath = Objects.requireNonNull(savePath);
        this.extendedMetricsEnabled = extendedMetricsEnabled;
    }

    @Override
    public Scoring fetchBaseScoringByRequestId(final String requestId) {
        try {
            return webClient.get()
                    .uri(fetchScoringPath, requestId)
                    .retrieve()
                    .bodyToMono(Scoring.class)
                    .block();
        } catch (RuntimeException ex) {
            throw new SimulationPersistenceException(LogMessage.SIMULATION_FETCH_BASE_SCORING_ERROR, ex);
        }
    }

    @Override
    public boolean existsScenarioName(final String requestId, final String scenarioName) {
        try {
            final SimulationExistsResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path(existsNamePath)
                            .queryParam("requestId", requestId)
                            .queryParam("scenarioName", scenarioName)
                            .build())
                    .retrieve()
                    .bodyToMono(SimulationExistsResponse.class)
                    .block();

            return response != null && response.isExists();
        } catch (RuntimeException ex) {
            throw new SimulationPersistenceException(LogMessage.SIMULATION_CHECK_NAME_ERROR, ex);
        }
    }

    @Override
    public Simulation saveSimulation(final Simulation simulation) {
        try {
            final SimulationPersistenceRequest requestBody = SimulationPersistenceRequest.fromSimulation(
                    simulation,
                    extendedMetricsEnabled);

            final Simulation persistedSimulation = webClient.post()
                    .uri(savePath)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Simulation.class)
                    .block();

            if (persistedSimulation == null) {
                return simulation;
            }
            return persistedSimulation;
        } catch (RuntimeException ex) {
            throw new SimulationPersistenceException(LogMessage.SIMULATION_SAVE_ERROR, ex);
        }
    }
}
