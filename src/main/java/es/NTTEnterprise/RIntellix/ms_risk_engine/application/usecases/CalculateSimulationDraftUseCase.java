package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CreditCardScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.CreditCardModelPayloadMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.LoanOrMortgageModelPayloadMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDraft;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RequestType;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.BaseRequestFetchException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.InvalidFormChangesException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.input.SimulationDraftPortService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.FetchRequestPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.FetchScoringPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.ModelPredictionPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.RiskCalculationStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.RiskCalculationStrategyFactory;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.FinancialMetricsCalculator;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case that orchestrates stateless simulation draft calculations.
 *
 * Implements the SimulationDraftPortService (domain input port) to properly expose
 * the use case functionality through the hexagonal architecture boundary.
 *
 * Follows the same pattern as ScoringProcessingService:
 * - Fetches base scoring data via output ports
 * - Maps form changes into request-level fields
 * - Invokes model prediction asynchronously
 * - Calculates deltas between base and simulated metrics
 *
 * @author Lucía Fernández Mancebo
 */
@Service
@Slf4j
public class CalculateSimulationDraftUseCase implements SimulationDraftPortService {

    private static final String CREDIT_CARD_REQUEST_TYPE = "CREDIT_CARD";

    private static final String[] REQUEST_TYPE_KEYS = { "requestType", "request_type", "type" };
    private static final String[] PARTY_ID_KEYS = { "partyId", "party_id" };
    private static final String[] AGE_KEYS = { "age", "edad" };
    private static final String[] GENDER_KEYS = { "gender", "genero" };
    private static final String[] MARITAL_STATUS_KEYS = { "maritalStatus", "estadoCivil", "estado_civil" };
    private static final String[] EDUCATION_KEYS = { "education", "educacion" };
    private static final String[] DEPENDENTS_KEYS = { "dependents", "nrDependants", "nr_dependants", "dependants" };
    private static final String[] HOME_OWNERSHIP_KEYS = { "homeOwnership", "home_ownership", "vivienda" };
    private static final String[] HAS_MORTGAGE_KEYS = { "hasMortgage", "has_mortgage", "tiene_hipoteca" };
    private static final String[] EMPLOYMENT_STATUS_KEYS = { "employmentStatus", "employment_status", "situacion_laboral" };
    private static final String[] OCCUPATION_SECTOR_KEYS = { "occupationSector", "occupation_sector", "sector_trabajo" };
    private static final String[] ANNUAL_INCOME_KEYS = { "annualIncome", "annual_income", "ingresos_anuales" };
    private static final String[] PURPOSE_KEYS = { "purpose", "proposito" };
    private static final String[] LOAN_TYPE_KEYS = { "loanType", "loan_type", "tipo_prestamo" };
    private static final String[] LOAN_AMOUNT_KEYS = { "loanAmount", "requestedAmount", "amount", "monto_prestamo" };
    private static final String[] TERM_MONTHS_KEYS = { "termMonths", "term_months", "plazo_meses" };
    private static final String[] INTEREST_RATE_KEYS = { "interestRate", "interest_rate", "tasa_interes" };
    private static final String[] LTV_KEYS = { "ltv", "loanToValue", "loan_to_value" };
    private static final String[] DTI_KEYS = { "dti" };
    private static final String[] PREVIOUS_LOANS_KEYS = { "previousLoansCount", "previous_loans_count", "num_prestamos_previos" };
    private static final String[] PREVIOUS_DEFAULTS_KEYS = { "previousDefaultsCount", "previous_defaults_count",
            "num_moras_previas" };
    private static final String[] CREDIT_LIMIT_KEYS = { "creditLimit", "requestedCreditLimit", "requested_credit_limit",
            "credit_limit", "amount" };
    private static final String[] IS_REVOLVING_KEYS = { "isRevolving", "is_revolving" };
    private static final String[] REVOLVING_PAYMENT_TYPE_KEYS = { "revolvingPaymentType", "revolving_payment_type" };
    private static final String[] REVOLVING_MINIMUM_PAYMENT_KEYS = { "revolvingMinimumPayment",
            "revolving_minimum_payment" };

    private final FetchScoringPort fetchScoringPort;
    private final FetchRequestPort fetchRequestPort;
    private final ModelPredictionPort modelPredictionPort;
    private final LoanOrMortgageModelPayloadMapper loanOrMortgageModelPayloadMapper;
    private final CreditCardModelPayloadMapper creditCardModelPayloadMapper;
    private final List<RiskCalculationStrategy> riskCalculationStrategies;
    private final String predictLoanPath;
    private final String predictCreditCardPath;

    /**
     * Constructor of the CalculateSimulationDraftUseCase class.
     *
     * @param fetchScoringPort               output port to fetch scoring data.
     * @param fetchRequestPort               output port to fetch base request data.
     * @param modelPredictionPort            output port to invoke model predictions.
     * @param loanOrMortgageModelPayloadMapper mapper for loan/mortgage model payloads.
     * @param creditCardModelPayloadMapper   mapper for credit-card model payloads.
     * @param riskCalculationStrategies      available risk calculation strategies.
     * @param predictLoanPath                model endpoint for loan/mortgage predictions.
     * @param predictCreditCardPath          model endpoint for credit-card predictions.
     */
    public CalculateSimulationDraftUseCase(
            final FetchScoringPort fetchScoringPort,
            final FetchRequestPort fetchRequestPort,
            final ModelPredictionPort modelPredictionPort,
            final LoanOrMortgageModelPayloadMapper loanOrMortgageModelPayloadMapper,
            final CreditCardModelPayloadMapper creditCardModelPayloadMapper,
            final List<RiskCalculationStrategy> riskCalculationStrategies,
            @Value("${risk.model.predict-loan-path:/api/v1/risk/predict-loan}") final String predictLoanPath,
            @Value("${risk.model.predict-credit-card-path:/api/v1/risk/predict-credit-card}") final String predictCreditCardPath) {
        this.fetchScoringPort = Objects.requireNonNull(fetchScoringPort);
        this.fetchRequestPort = Objects.requireNonNull(fetchRequestPort);
        this.modelPredictionPort = Objects.requireNonNull(modelPredictionPort);
        this.loanOrMortgageModelPayloadMapper = Objects.requireNonNull(loanOrMortgageModelPayloadMapper);
        this.creditCardModelPayloadMapper = Objects.requireNonNull(creditCardModelPayloadMapper);
        this.riskCalculationStrategies = Objects.requireNonNull(riskCalculationStrategies);
        this.predictLoanPath = Objects.requireNonNull(predictLoanPath);
        this.predictCreditCardPath = Objects.requireNonNull(predictCreditCardPath);
    }

    /**
     * Calculates a simulation draft without persistence.
     *
     * Process flow (following hexagonal architecture):
     * 1. Fetch base scoring data for the request
     * 2. Fetch base request data for request type and input features
     * 3. Merge base data with form changes to build model payload
     * 4. Invoke model prediction asynchronously to obtain new PD
     * 5. Calculate deltas by comparing simulated metrics with base metrics
     *
     * @param requestId   the request identifier.
     * @param formChanges the user-specified modified values.
     * @return the simulation draft with simulated metrics and deltas.
     * @throws ScoringNotFoundException     if scoring data cannot be retrieved
     * @throws InvalidFormChangesException  if form changes are invalid
     */
    @Override
    public SimulationDraft calculateDraft(final String requestId, final FormChanges formChanges) {

        if (formChanges == null || formChanges.getValues() == null || formChanges.getValues().isEmpty()) {
            throw new InvalidFormChangesException("formChanges is required");
        }

        final Scoring baseScoring = fetchScoringPort.fetchByRequestId(requestId);
        if (baseScoring == null || baseScoring.getResults() == null) {
            throw new ScoringNotFoundException(LogMessage.SCORING_RETRIEVING_MESSAGE_EXCEPTION + requestId);
        }

        final Map<String, Object> baseRequest = fetchRequestPort.fetchByRequestId(requestId);
        if (baseRequest == null || baseRequest.isEmpty()) {
            throw new BaseRequestFetchException("Base request not found for requestId: " + requestId);
        }

        final RequestType requestType = resolveRequestType(baseRequest);
        final String normalizedRequestType = requestType.getValue();
        final Boolean isRevolving = resolveIsRevolving(formChanges.getValues(), baseRequest, requestType);

        final SimulationValues baseValues = resolveSimulationValues(baseRequest, null, requestType, isRevolving);
        final SimulationValues simulatedValues = resolveSimulationValues(baseRequest, formChanges.getValues(),
                requestType, isRevolving);

        final Map<String, Object> modelPayload = buildModelPayload(requestId, normalizedRequestType,
                baseRequest, formChanges.getValues(), requestType, isRevolving);

        final CompletableFuture<ModelPredictionResult> predictionResultFuture = modelPredictionPort
                .predictAsync(modelPayload, requestId, resolveModelEndpointPath(requestType));

        final RiskCalculationStrategy riskStrategy = RiskCalculationStrategyFactory.createStrategy(
                normalizedRequestType, isRevolving, riskCalculationStrategies);
        final RiskMetrics prePdMetrics = riskStrategy.calculatePrePdMetrics(
                simulatedValues.getRequestedAmount(), simulatedValues.getLtv());

        final ModelPredictionResult predictionResult = predictionResultFuture.join();
        final RiskMetrics fullMetrics = riskStrategy.assembleFullMetrics(
                predictionResult.getProbabilityOfDefault(),
                prePdMetrics,
                simulatedValues.getRequestedAmount(),
                simulatedValues.getAnnualIncome(),
                simulatedValues.getTermMonths(),
                simulatedValues.getInterestRate());

        final SimulationMetrics simulatedMetrics = buildSimulationMetrics(fullMetrics, requestType, simulatedValues);
        final SimulationDelta delta = buildDelta(baseScoring.getResults(), baseValues, simulatedMetrics, requestType);

        return new SimulationDraft(simulatedMetrics, delta);
    }

    private RequestType resolveRequestType(final Map<String, Object> baseRequest) {
        final String rawType = getString(null, baseRequest, REQUEST_TYPE_KEYS);
        if (rawType == null || rawType.isBlank()) {
            throw new InvalidFormChangesException("requestType is required");
        }
        if (CREDIT_CARD_REQUEST_TYPE.equalsIgnoreCase(rawType.trim())) {
            return RequestType.TARJETA_CREDITO;
        }
        try {
            return RequestType.fromValue(rawType.trim());
        } catch (IllegalArgumentException ex) {
            throw new InvalidFormChangesException("Invalid requestType: " + rawType, ex);
        }
    }

    private Boolean resolveIsRevolving(final Map<String, Object> formChanges,
            final Map<String, Object> baseRequest,
            final RequestType requestType) {
        if (requestType != RequestType.TARJETA_CREDITO) {
            return null;
        }
        return getBoolean(formChanges, baseRequest, IS_REVOLVING_KEYS);
    }

    private Map<String, Object> buildModelPayload(final String requestId,
            final String normalizedRequestType,
            final Map<String, Object> baseRequest,
            final Map<String, Object> formChanges,
            final RequestType requestType,
            final Boolean isRevolving) {
        if (requestType == RequestType.TARJETA_CREDITO) {
            final CreditCardScoringGenerationRequest creditCardRequest = buildCreditCardRequest(
                    requestId, normalizedRequestType, baseRequest, formChanges, isRevolving);
            return creditCardModelPayloadMapper.toModelPayload(creditCardRequest, normalizedRequestType);
        }
        final ScoringGenerationRequest loanRequest = buildLoanOrMortgageRequest(
                requestId, normalizedRequestType, baseRequest, formChanges);
        return loanOrMortgageModelPayloadMapper.toModelPayload(loanRequest);
    }

    private ScoringGenerationRequest buildLoanOrMortgageRequest(final String requestId,
            final String normalizedRequestType,
            final Map<String, Object> baseRequest,
            final Map<String, Object> formChanges) {
        return new ScoringGenerationRequest(
                requestId,
                normalizedRequestType,
                getString(formChanges, baseRequest, PARTY_ID_KEYS),
                getInteger(formChanges, baseRequest, AGE_KEYS),
                getString(formChanges, baseRequest, GENDER_KEYS),
                getString(formChanges, baseRequest, MARITAL_STATUS_KEYS),
                getString(formChanges, baseRequest, EDUCATION_KEYS),
                getInteger(formChanges, baseRequest, DEPENDENTS_KEYS),
                getString(formChanges, baseRequest, HOME_OWNERSHIP_KEYS),
                getBoolean(formChanges, baseRequest, HAS_MORTGAGE_KEYS),
                getString(formChanges, baseRequest, EMPLOYMENT_STATUS_KEYS),
                getString(formChanges, baseRequest, OCCUPATION_SECTOR_KEYS),
                getDouble(formChanges, baseRequest, ANNUAL_INCOME_KEYS),
                getString(formChanges, baseRequest, PURPOSE_KEYS),
                getDouble(formChanges, baseRequest, LOAN_AMOUNT_KEYS),
                getString(formChanges, baseRequest, LOAN_TYPE_KEYS),
                getInteger(formChanges, baseRequest, TERM_MONTHS_KEYS),
                getDouble(formChanges, baseRequest, INTEREST_RATE_KEYS),
                getDouble(formChanges, baseRequest, LTV_KEYS),
                getDouble(formChanges, baseRequest, DTI_KEYS),
                getInteger(formChanges, baseRequest, PREVIOUS_LOANS_KEYS),
                getInteger(formChanges, baseRequest, PREVIOUS_DEFAULTS_KEYS));
    }

    private CreditCardScoringGenerationRequest buildCreditCardRequest(final String requestId,
            final String normalizedRequestType,
            final Map<String, Object> baseRequest,
            final Map<String, Object> formChanges,
            final Boolean isRevolving) {
        return new CreditCardScoringGenerationRequest(
                requestId,
                normalizedRequestType,
                getString(formChanges, baseRequest, PARTY_ID_KEYS),
                getInteger(formChanges, baseRequest, AGE_KEYS),
                getString(formChanges, baseRequest, GENDER_KEYS),
                getString(formChanges, baseRequest, MARITAL_STATUS_KEYS),
                getString(formChanges, baseRequest, EMPLOYMENT_STATUS_KEYS),
                getDouble(formChanges, baseRequest, ANNUAL_INCOME_KEYS),
                getDouble(formChanges, baseRequest, CREDIT_LIMIT_KEYS),
                isRevolving);
    }

    private String resolveModelEndpointPath(final RequestType requestType) {
        return requestType == RequestType.TARJETA_CREDITO ? predictCreditCardPath : predictLoanPath;
    }

    private SimulationMetrics buildSimulationMetrics(final RiskMetrics fullMetrics,
            final RequestType requestType,
            final SimulationValues simulationValues) {
        final SimulationMetrics metrics = new SimulationMetrics();
        metrics.setPd(fullMetrics.getPd());
        metrics.setLgd(fullMetrics.getLgd());
        metrics.setEad(fullMetrics.getEad());
        metrics.setEcl(fullMetrics.getEcl());
        metrics.setRiskGrade(fullMetrics.getRiskLevel());

        if (requestType == RequestType.TARJETA_CREDITO) {
            applyCreditCardFinancials(metrics, simulationValues);
        } else {
            applyLoanFinancials(metrics, simulationValues);
        }
        return metrics;
    }

    private void applyLoanFinancials(final SimulationMetrics metrics,
            final SimulationValues simulationValues) {
        final Double principal = simulationValues.getRequestedAmount();
        final Double annualRate = simulationValues.getInterestRate();
        final Integer termMonths = simulationValues.getTermMonths();
        final Double annualIncome = simulationValues.getAnnualIncome();

        final double monthlyPayment = FinancialMetricsCalculator.calculateMonthlyPayment(principal, annualRate, termMonths);
        final double dti = FinancialMetricsCalculator.calculateDti(monthlyPayment, annualIncome);
        final double totalPayment = FinancialMetricsCalculator.calculateTotalPayment(monthlyPayment, termMonths);
        final double totalInterest = FinancialMetricsCalculator.calculateTotalInterest(totalPayment, principal);
        final double disposableIncome = FinancialMetricsCalculator.calculateDisposableIncome(annualIncome, monthlyPayment);

        metrics.setMonthlyPayment(monthlyPayment);
        metrics.setDti(dti);
        metrics.setTotalPayment(totalPayment);
        metrics.setTotalInterest(totalInterest);
        metrics.setDisposableIncome(disposableIncome);
    }

    private void applyCreditCardFinancials(final SimulationMetrics metrics,
            final SimulationValues simulationValues) {
        final Double creditLimit = simulationValues.getRequestedAmount();
        final Double annualIncome = simulationValues.getAnnualIncome();
        final Double minimumPayment = resolveMinimumPayment(simulationValues);

        Double monthlyPayment = null;
        if (Boolean.TRUE.equals(simulationValues.getIsRevolving())) {
            monthlyPayment = minimumPayment;
        } else if (creditLimit != null && creditLimit > 0.0) {
            monthlyPayment = creditLimit;
        }

        if (monthlyPayment != null) {
            metrics.setMonthlyPayment(monthlyPayment);
            metrics.setDti(FinancialMetricsCalculator.calculateDti(monthlyPayment, annualIncome));
            metrics.setDisposableIncome(FinancialMetricsCalculator.calculateDisposableIncome(annualIncome, monthlyPayment));
        }
    }

    private Double resolveMinimumPayment(final SimulationValues simulationValues) {
        final Double creditLimit = simulationValues.getRequestedAmount();
        final Double minimumPayment = simulationValues.getRevolvingMinimumPayment();
        if (creditLimit == null || creditLimit <= 0.0 || minimumPayment == null) {
            return null;
        }
        final String paymentType = simulationValues.getRevolvingPaymentType();
        if (paymentType != null
                && paymentType.equalsIgnoreCase(SimulationConstants.REVOLVING_PAYMENT_TYPE_PERCENTAGE)) {
            return creditLimit * (minimumPayment / 100.0);
        }
        return minimumPayment;
    }

    private SimulationDelta buildDelta(final es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.RiskMetrics baseMetrics,
            final SimulationValues baseValues,
            final SimulationMetrics simulatedMetrics,
            final RequestType requestType) {
        final SimulationMetrics baseFinancials = new SimulationMetrics();
        if (requestType == RequestType.TARJETA_CREDITO) {
            applyCreditCardFinancials(baseFinancials, baseValues);
        } else {
            applyLoanFinancials(baseFinancials, baseValues);
        }

        final SimulationDelta delta = new SimulationDelta();
        delta.setPdChange(getSafe(simulatedMetrics.getPd()) - getSafe(baseMetrics.getProbabilityOfDefault()));
        delta.setEclChange(getSafe(simulatedMetrics.getEcl()) - getSafe(baseMetrics.getExpectedCalculatedLoss()));
        final String baseRiskGradeName = baseMetrics.getRiskLevel() != null ? baseMetrics.getRiskLevel() : "UNKNOWN";
        delta.setRiskGradeChange(baseRiskGradeName + SimulationConstants.RISK_GRADE_ARROW + simulatedMetrics.getRiskGrade());
        delta.setMonthlyPaymentChange(getSafe(simulatedMetrics.getMonthlyPayment()) - getSafe(baseFinancials.getMonthlyPayment()));
        delta.setDtiChange(getSafe(simulatedMetrics.getDti()) - getSafe(baseFinancials.getDti()));
        return delta;
    }

    private SimulationValues resolveSimulationValues(final Map<String, Object> baseRequest,
            final Map<String, Object> formChanges,
            final RequestType requestType,
            final Boolean isRevolving) {
        final Double annualIncome = getDouble(formChanges, baseRequest, ANNUAL_INCOME_KEYS);
        final Double interestRate = getDouble(formChanges, baseRequest, INTEREST_RATE_KEYS);
        Integer termMonths = getInteger(formChanges, baseRequest, TERM_MONTHS_KEYS);
        if (requestType != RequestType.TARJETA_CREDITO && termMonths == null) {
            termMonths = SimulationConstants.MIN_TERM_MONTHS;
        }
        final Double ltv = getDouble(formChanges, baseRequest, LTV_KEYS);
        final Double requestedAmount = requestType == RequestType.TARJETA_CREDITO
                ? getDouble(formChanges, baseRequest, CREDIT_LIMIT_KEYS)
                : getDouble(formChanges, baseRequest, LOAN_AMOUNT_KEYS);
        final Double revolvingMinimumPayment = requestType == RequestType.TARJETA_CREDITO
                ? getDouble(formChanges, baseRequest, REVOLVING_MINIMUM_PAYMENT_KEYS)
                : null;
        final String revolvingPaymentType = requestType == RequestType.TARJETA_CREDITO
                ? getString(formChanges, baseRequest, REVOLVING_PAYMENT_TYPE_KEYS)
                : null;
        return new SimulationValues(requestedAmount, annualIncome, interestRate, termMonths,
                ltv, isRevolving, revolvingMinimumPayment, revolvingPaymentType);
    }

    private String getString(final Map<String, Object> overrides,
            final Map<String, Object> base,
            final String... keys) {
        final Object value = resolveValue(overrides, base, keys);
        return value == null ? null : String.valueOf(value);
    }

    private Integer getInteger(final Map<String, Object> overrides,
            final Map<String, Object> base,
            final String... keys) {
        final Object value = resolveValue(overrides, base, keys);
        if (value == null) {
            return null;
        }
        if (value instanceof Number numberValue) {
            return numberValue.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException ex) {
            throw new InvalidFormChangesException("Invalid numeric value for key: " + keys[0]);
        }
    }

    private Double getDouble(final Map<String, Object> overrides,
            final Map<String, Object> base,
            final String... keys) {
        final Object value = resolveValue(overrides, base, keys);
        if (value == null) {
            return null;
        }
        if (value instanceof Number numberValue) {
            return numberValue.doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException ex) {
            throw new InvalidFormChangesException("Invalid numeric value for key: " + keys[0]);
        }
    }

    private Boolean getBoolean(final Map<String, Object> overrides,
            final Map<String, Object> base,
            final String... keys) {
        final Object value = resolveValue(overrides, base, keys);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean boolValue) {
            return boolValue;
        }
        return Boolean.parseBoolean(value.toString());
    }

    private Object resolveValue(final Map<String, Object> overrides,
            final Map<String, Object> base,
            final String... keys) {
        final Object overrideValue = findValue(overrides, keys);
        if (overrideValue != null) {
            return overrideValue;
        }
        return findValue(base, keys);
    }

    private Object findValue(final Map<String, Object> source, final String... keys) {
        if (source == null || keys == null) {
            return null;
        }
        for (String key : keys) {
            if (key == null) {
                continue;
            }
            final Object directValue = source.get(key);
            if (directValue != null) {
                return directValue;
            }
        }
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            final String entryKey = normalizeKey(entry.getKey());
            for (String key : keys) {
                if (normalizeKey(key).equals(entryKey)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private String normalizeKey(final String key) {
        if (key == null) {
            return "";
        }
        return key.replace("_", "")
                .replace("-", "")
                .replace(" ", "")
                .toLowerCase();
    }

    private double getSafe(final Double value) {
        return value == null ? SimulationConstants.ZERO_VALUE : value;
    }

    private static final class SimulationValues {
        private final Double requestedAmount;
        private final Double annualIncome;
        private final Double interestRate;
        private final Integer termMonths;
        private final Double ltv;
        private final Boolean isRevolving;
        private final Double revolvingMinimumPayment;
        private final String revolvingPaymentType;

        private SimulationValues(final Double requestedAmount,
                final Double annualIncome,
                final Double interestRate,
                final Integer termMonths,
                final Double ltv,
                final Boolean isRevolving,
                final Double revolvingMinimumPayment,
                final String revolvingPaymentType) {
            this.requestedAmount = requestedAmount;
            this.annualIncome = annualIncome;
            this.interestRate = interestRate;
            this.termMonths = termMonths;
            this.ltv = ltv;
            this.isRevolving = isRevolving;
            this.revolvingMinimumPayment = revolvingMinimumPayment;
            this.revolvingPaymentType = revolvingPaymentType;
        }

        private Double getRequestedAmount() {
            return requestedAmount;
        }

        private Double getAnnualIncome() {
            return annualIncome;
        }

        private Double getInterestRate() {
            return interestRate;
        }

        private Integer getTermMonths() {
            return termMonths;
        }

        private Double getLtv() {
            return ltv;
        }

        private Boolean getIsRevolving() {
            return isRevolving;
        }

        private Double getRevolvingMinimumPayment() {
            return revolvingMinimumPayment;
        }

        private String getRevolvingPaymentType() {
            return revolvingPaymentType;
        }
    }
}
