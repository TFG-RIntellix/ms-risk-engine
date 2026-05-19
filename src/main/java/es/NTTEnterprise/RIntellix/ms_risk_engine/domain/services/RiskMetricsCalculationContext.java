package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services;

import java.util.Map;
import java.util.Objects;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;

/**
 * Context object for risk metrics calculation.
 * 
 * Encapsulates all necessary data for calculating risk metrics, abstracting
 * away whether the calculation is for scoring or simulation.
 *
 * This is a domain value object that represents the complete set of parameters
 * needed to execute risk metric calculations. It follows the Value Object
 * pattern
 * to aggregate all calculation inputs in a single, immutable structure.
 *
 * This pattern enables the domain service to be used in multiple application
 * contexts without coupling to specific use cases.
 *
 * Properties include:
 * - modelPayload: The payload to send to the ML model API
 * - requestId: Request identifier for tracing and logging
 * - modelEndpointPath: The endpoint path for model invocation
 * - requestType: Type of request (LOAN, MORTGAGE, CREDIT_CARD)
 * - loanAmount: Principal amount or credit limit
 * - ltv: Loan-to-Value ratio (null for credit cards)
 * - annualIncome: Borrower's annual income
 * - termMonths: Loan term in months (null for credit cards)
 * - interestRate: Annual interest rate
 *
 * @author Lucia Fernandez Mancebo
 * @Date 09-05-2026
 */
public class RiskMetricsCalculationContext {

    private final Map<String, Object> modelPayload;
    private final String requestId;
    private final String modelEndpointPath;
    private final String requestType;

    /**
     * Constructor of the RiskMetricsCalculationContext class.
     *
     * @param modelPayload      the payload to send to the model API.
     * @param requestId         the request identifier.
     * @param modelEndpointPath the model endpoint path (e.g.,
     *                          /api/v1/risk/predict-loan).
     * @param requestType       the request type (PRESTAMO, HIPOTECA,
     *                          TARJETA_CREDITO).
     * @param loanAmount        the loan/credit amount.
     * @param ltv               the loan-to-value ratio (null for credit cards).
     * @param annualIncome      the borrower's annual income.
     * @param termMonths        the loan term in months (null for credit cards).
     * @param interestRate      the annual interest rate.
     * @throws NullPointerException if any required parameter is null
     */
    public RiskMetricsCalculationContext(
            final Map<String, Object> modelPayload,
            final String requestId,
            final String modelEndpointPath,
            final String requestType) {
        this.modelPayload = Objects.requireNonNull(modelPayload,
                LogMessage.MODEL_PAYLOAD_NULL);
        this.requestId = Objects.requireNonNull(requestId,
                LogMessage.REQUEST_ID_NULL_ERROR);
        this.modelEndpointPath = Objects.requireNonNull(modelEndpointPath,
                LogMessage.ENDPOINT_PATH_NULL_ERROR);
        this.requestType = Objects.requireNonNull(requestType,
                LogMessage.REQUEST_TYPE_NULL_ERROR);
    }

    /**
     * Gets the model payload to be sent to the ML model API.
     *
     * @return the model payload map
     */
    public Map<String, Object> getModelPayload() {
        return modelPayload;
    }

    /**
     * Gets the request identifier used for tracing and logging.
     *
     * @return the request identifier
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Gets the model endpoint path for API invocation.
     *
     * @return the endpoint path
     */
    public String getModelEndpointPath() {
        return modelEndpointPath;
    }

    /**
     * Gets the request type (LOAN, MORTGAGE, CREDIT_CARD).
     *
     * @return the request type
     */
    public String getRequestType() {
        return requestType;
    }
}
