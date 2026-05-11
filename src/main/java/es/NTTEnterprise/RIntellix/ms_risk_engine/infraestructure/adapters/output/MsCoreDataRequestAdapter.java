package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.FetchRequestPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.BaseRequestFetchException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output.clients.MsCoreDataClient;

@Component
public class MsCoreDataRequestAdapter implements FetchRequestPort {

    private static final String PARTY_KEY = "party";

    private final MsCoreDataClient msCoreDataClient;

    public MsCoreDataRequestAdapter(final MsCoreDataClient msCoreDataClient) {
        this.msCoreDataClient = Objects.requireNonNull(msCoreDataClient);
    }

    @Override
    public Map<String, Object> fetchByRequestId(final String requestId) {
        try {
            final Map<String, Object> response = msCoreDataClient.getRequestById(requestId);
            final Map<String, Object> flattened = new HashMap<>();
            if (response != null) {
                flattened.putAll(response);
                final Object party = response.get(PARTY_KEY);
                if (party instanceof Map<?, ?> partyMap) {
                    partyMap.forEach((key, value) -> flattened.put(String.valueOf(key), value));
                }
            }
            return flattened;
        } catch (RuntimeException ex) {
            throw new BaseRequestFetchException("Failed to fetch base request for requestId: " + requestId, ex);
        }
    }
}
