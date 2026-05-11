package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.CreditCardModelPayloadMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers.LoanOrMortgageModelPayloadMapper;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.ModelInputs;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDraft;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.FetchRequestPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.FetchScoringPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.ModelPredictionPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.RiskCalculationStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.LoanRiskCalculationStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.MortgageRiskCalculationStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.RevolvingCreditCardRiskCalculationStrategy;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.strategies.StandardCreditCardRiskCalculationStrategy;

@DisplayName("CalculateSimulationDraftUseCase")
class CalculateSimulationDraftUseCaseTest {

    @Test
    @DisplayName("Given missing scoring when calculating draft then throw ScoringNotFoundException")
    void givenMissingScoring_whenCalculatingDraft_thenThrowScoringNotFound() {
        FetchScoringPort fetchScoringPort = requestId -> null;
        FetchRequestPort fetchRequestPort = requestId -> Map.of("requestType", "PRESTAMO");
        ModelPredictionPort modelPredictionPort = (payload, requestId, endpoint) ->
                CompletableFuture.completedFuture(new ModelPredictionResult(0.01, null, null, null));

        CalculateSimulationDraftUseCase useCase = buildUseCase(fetchScoringPort, fetchRequestPort, modelPredictionPort);

        assertThatThrownBy(() -> useCase.calculateDraft("REQ-1", new FormChanges(Map.of("amount", 1000.0))))
                .isInstanceOf(ScoringNotFoundException.class);
    }

    @Test
    @DisplayName("Given valid inputs when calculating draft then return calculated simulation")
    void givenValidInputs_whenCalculatingDraft_thenReturnSimulationDraft() {
        Scoring baseScoring = createBaseScoring();

        FetchScoringPort fetchScoringPort = requestId -> baseScoring;
        FetchRequestPort fetchRequestPort = requestId -> createLoanRequest();
        ModelPredictionPort modelPredictionPort = (payload, requestId, endpoint) ->
                CompletableFuture.completedFuture(new ModelPredictionResult(0.012, null, null, null));

        CalculateSimulationDraftUseCase useCase = buildUseCase(fetchScoringPort, fetchRequestPort, modelPredictionPort);

        SimulationDraft result = useCase.calculateDraft("REQ-1", new FormChanges(Map.of("interest_rate", 3.2)));

        assertThat(result).isNotNull();
        assertThat(result.getSimulatedResults().getPd()).isEqualTo(0.012);
        assertThat(result.getSimulatedResults().getMonthlyPayment()).isGreaterThan(0.0);
        assertThat(result.getDelta().getRiskGradeChange()).contains("->");
    }

    @Test
    @DisplayName("Given credit card simulation when calculating draft then use credit limit changes")
    void givenCreditCardSimulation_whenCalculatingDraft_thenUseCreditLimitChanges() {
        Scoring baseScoring = createBaseScoring();

        FetchScoringPort fetchScoringPort = requestId -> baseScoring;
        FetchRequestPort fetchRequestPort = requestId -> createCreditCardRequest();
        ModelPredictionPort modelPredictionPort = (payload, requestId, endpoint) ->
                CompletableFuture.completedFuture(new ModelPredictionResult(0.08, null, null, null));

        CalculateSimulationDraftUseCase useCase = buildUseCase(fetchScoringPort, fetchRequestPort, modelPredictionPort);

        SimulationDraft result = useCase.calculateDraft("REQ-1",
                new FormChanges(Map.of("requested_credit_limit", 1000.0)));

        assertThat(result.getSimulatedResults().getEad()).isEqualTo(500.0);
        assertThat(result.getSimulatedResults().getPd()).isEqualTo(0.08);
    }

    private Scoring createBaseScoring() {
        ModelInputs inputs = new ModelInputs(new HashMap<>(Map.of(
                "annualIncome", 45000.0,
                "interestRate", 3.5,
                "termMonths", 240,
                "requestedAmount", 150000.0)));

        RiskMetrics metrics = new RiskMetrics();
        metrics.setProbabilityOfDefault(0.017);
        metrics.setLossGivenDefault(0.45);
        metrics.setExposureAtDefault(150000.0);
        metrics.setExpectedCalculatedLoss(1150.0);
        metrics.setRiskLevel("B");

        Scoring scoring = new Scoring();
        scoring.setRequestId("REQ-1");
        scoring.setInputSnapshot(inputs);
        scoring.setResults(metrics);
        return scoring;
    }

    private CalculateSimulationDraftUseCase buildUseCase(
            final FetchScoringPort fetchScoringPort,
            final FetchRequestPort fetchRequestPort,
            final ModelPredictionPort modelPredictionPort) {
        final List<RiskCalculationStrategy> strategies = List.of(
                new LoanRiskCalculationStrategy(),
                new MortgageRiskCalculationStrategy(),
                new StandardCreditCardRiskCalculationStrategy(),
                new RevolvingCreditCardRiskCalculationStrategy());
        return new CalculateSimulationDraftUseCase(
                fetchScoringPort,
                fetchRequestPort,
                modelPredictionPort,
                new LoanOrMortgageModelPayloadMapper(),
                new CreditCardModelPayloadMapper(),
                strategies,
                "/api/v1/risk/predict-loan",
                "/api/v1/risk/predict-credit-card");
    }

    private Map<String, Object> createLoanRequest() {
        return Map.of(
                "requestType", "PRESTAMO",
                "loanAmount", 150000.0,
                "termMonths", 240,
                "interestRate", 3.5,
                "annualIncome", 45000.0,
                "ltv", 0.75,
                "employmentStatus", "PERMANENT");
    }

    private Map<String, Object> createCreditCardRequest() {
        return Map.of(
                "requestType", "TARJETA DE CREDITO",
                "creditLimit", 2000.0,
                "annualIncome", 32000.0,
                "isRevolving", false);
    }
}
