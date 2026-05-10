package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.output.PredictPdPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ModelPredictionException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output.clients.MsModelClient;

@Component
public class MsModelPredictPdAdapter implements PredictPdPort {

    private static final String PROBABILITY_OF_DEFAULT_KEY = "probability_of_default";
    private static final String PD_KEY = "pd";

    private final MsModelClient msModelClient;

    public MsModelPredictPdAdapter(final MsModelClient msModelClient) {
        this.msModelClient = Objects.requireNonNull(msModelClient);
    }

    @Override
    public Double predictPd(final Map<String, Object> mergedVariables, final String requestId) {
        try {
            final Map<String, Object> response = msModelClient.predictPd(mergedVariables);
            if (response == null || response.isEmpty()) {
                throw new ModelPredictionException("Empty model response for requestId: " + requestId,
                        HttpStatus.BAD_GATEWAY.value());
            }
            final Object pd = response.getOrDefault(PROBABILITY_OF_DEFAULT_KEY, response.get(PD_KEY));
            if (pd == null) {
                throw new ModelPredictionException("PD value missing in model response for requestId: " + requestId,
                        HttpStatus.BAD_GATEWAY.value());
            }
            if (pd instanceof Number numberValue) {
                return numberValue.doubleValue();
            }
            return Double.parseDouble(pd.toString());
        } catch (ModelPredictionException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new ModelPredictionException(
                    "Failed to predict PD for requestId: " + requestId,
                    HttpStatus.BAD_GATEWAY.value(),
                    ex);
        }
    }
}
