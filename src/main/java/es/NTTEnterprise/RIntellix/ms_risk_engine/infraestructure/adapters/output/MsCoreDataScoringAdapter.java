package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output;

import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.output.FetchScoringPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.BaseScoringData;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output.clients.MsCoreDataClient;

@Component
public class MsCoreDataScoringAdapter implements FetchScoringPort {

    private static final String RESULTS_KEY = "results";
    private static final String INPUT_SNAPSHOT_KEY = "inputSnapshot";
    private static final String PD_KEY = "probabilityOfDefault";
    private static final String LGD_KEY = "lossGivenDefault";
    private static final String EAD_KEY = "exposureAtDefault";
    private static final String ECL_KEY = "expectedCalculatedLoss";
    private static final String RISK_LEVEL_KEY = "riskLevel";

    private final MsCoreDataClient msCoreDataClient;

    public MsCoreDataScoringAdapter(final MsCoreDataClient msCoreDataClient) {
        this.msCoreDataClient = Objects.requireNonNull(msCoreDataClient);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BaseScoringData fetchByRequestId(final String requestId) {
        final Map<String, Object> response = msCoreDataClient.getScoringByRequestId(requestId);
        if (response == null || response.isEmpty()) {
            throw new ScoringNotFoundException("Scoring not found for requestId: " + requestId);
        }

        final Map<String, Object> resultsMap = response.get(RESULTS_KEY) instanceof Map<?, ?>
                ? (Map<String, Object>) response.get(RESULTS_KEY)
                : response;

        final SimulationMetrics metrics = new SimulationMetrics();
        metrics.setPd(getDouble(resultsMap, PD_KEY, "pd"));
        metrics.setLgd(getDouble(resultsMap, LGD_KEY, "lgd"));
        metrics.setEad(getDouble(resultsMap, EAD_KEY, "ead"));
        metrics.setEcl(getDouble(resultsMap, ECL_KEY, "ecl"));
        metrics.setRiskGrade(getString(resultsMap, RISK_LEVEL_KEY, "riskGrade"));

        final Map<String, Object> inputSnapshot = response.get(INPUT_SNAPSHOT_KEY) instanceof Map<?, ?>
                ? (Map<String, Object>) response.get(INPUT_SNAPSHOT_KEY)
                : Map.of();

        return new BaseScoringData(metrics, inputSnapshot);
    }

    private Double getDouble(final Map<String, Object> source, final String primaryKey, final String fallbackKey) {
        final Object value = source.getOrDefault(primaryKey, source.get(fallbackKey));
        if (value == null) {
            return null;
        }
        if (value instanceof Number numberValue) {
            return numberValue.doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    private String getString(final Map<String, Object> source, final String primaryKey, final String fallbackKey) {
        final Object value = source.getOrDefault(primaryKey, source.get(fallbackKey));
        return value == null ? null : value.toString();
    }
}
