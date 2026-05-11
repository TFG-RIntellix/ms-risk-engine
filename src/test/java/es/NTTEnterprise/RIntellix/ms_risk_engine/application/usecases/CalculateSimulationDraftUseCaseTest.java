package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.ModelInputs;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDraft;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.FetchScoringPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.ModelPredictionPort;

@DisplayName("CalculateSimulationDraftUseCase")
class CalculateSimulationDraftUseCaseTest {

    @Test
    @DisplayName("Given missing scoring when calculating draft then throw ScoringNotFoundException")
    void givenMissingScoring_whenCalculatingDraft_thenThrowScoringNotFound() {
        FetchScoringPort fetchScoringPort = requestId -> null;
        ModelPredictionPort modelPredictionPort = (payload, requestId, endpoint) -> 
            CompletableFuture.completedFuture(new ModelPredictionResult(0.01, null, null, null));

        CalculateSimulationDraftUseCase useCase = new CalculateSimulationDraftUseCase(
                fetchScoringPort,
                modelPredictionPort);

        assertThatThrownBy(() -> useCase.calculateDraft("REQ-1", new FormChanges(Map.of())))
                .isInstanceOf(ScoringNotFoundException.class);
    }

    @Test
    @DisplayName("Given valid inputs when calculating draft then return calculated simulation")
    void givenValidInputs_whenCalculatingDraft_thenReturnSimulationDraft() {
        Scoring baseScoring = createBaseScoring();

        FetchScoringPort fetchScoringPort = requestId -> baseScoring;
        ModelPredictionPort modelPredictionPort = (payload, requestId, endpoint) -> 
            CompletableFuture.completedFuture(new ModelPredictionResult(0.012, null, null, null));

        CalculateSimulationDraftUseCase useCase = new CalculateSimulationDraftUseCase(
                fetchScoringPort,
                modelPredictionPort);

        SimulationDraft result = useCase.calculateDraft("REQ-1", new FormChanges(Map.of("interestRate", 3.2)));

        assertThat(result).isNotNull();
        assertThat(result.getSimulatedResults().getPd()).isEqualTo(0.012);
        assertThat(result.getSimulatedResults().getMonthlyPayment()).isGreaterThan(0.0);
        assertThat(result.getDelta().getRiskGradeChange()).contains("->");
    }

    private Scoring createBaseScoring() {
        ModelInputs inputs = new ModelInputs(new HashMap<>(Map.of(
                "annualIncome", 45000.0,
                "interestRate", 3.5,
                "termMonths", 240,
                "requestedAmount", 150000.0)));

        RiskMetrics metrics = new RiskMetrics();
        metrics.setPd(0.017);
        metrics.setLgd(0.45);
        metrics.setEad(150000.0);
        metrics.setEcl(1150.0);
        metrics.setRiskGrade("B");

        Scoring scoring = new Scoring();
        scoring.setRequestId("REQ-1");
        scoring.setInputSnapshot(inputs);
        scoring.setResults(metrics);
        return scoring;
    }
}
