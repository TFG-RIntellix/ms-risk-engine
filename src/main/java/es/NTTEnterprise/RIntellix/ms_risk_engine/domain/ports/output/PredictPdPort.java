package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output;

import java.util.Map;

/**
 * Port interface for predicting the Probability of Default (PD) based on merged
 * input variables.
 * This interface defines the contract for any implementation that provides PD
 * prediction functionality.
 * Implementations of this port will take a map of merged variables and a
 * request ID, and return the predicted PD as a Double.
 * 
 * @author Lucía Fernández Mancebo
 * @Date 03-03-2026
 */
public interface PredictPdPort {
    Double predictPd(Map<String, Object> mergedVariables, String requestId);
}
