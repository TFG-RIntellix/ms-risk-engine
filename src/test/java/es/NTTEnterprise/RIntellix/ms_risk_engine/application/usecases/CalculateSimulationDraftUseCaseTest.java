package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.output.FetchRequestPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.output.FetchScoringPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.ports.output.PredictPdPort;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.BaseScoringData;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationDraft;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.simulation.SimulationMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.ScoringNotFoundException;

@DisplayName("CalculateSimulationDraftUseCase")
class CalculateSimulationDraftUseCaseTest {

    @Test
    @DisplayName("Given missing scoring when calculating draft then throw ScoringNotFoundException")
    void givenMissingScoring_whenCalculatingDraft_thenThrowScoringNotFound() {
        FetchRequestPort fetchRequestPort = requestId -> Map.of();
        FetchScoringPort fetchScoringPort = requestId -> null;
        PredictPdPort predictPdPort = (payload, requestId) -> 0.01;

        CalculateSimulationDraftUseCase useCase = new CalculateSimulationDraftUseCase(
                fetchRequestPort,
                fetchScoringPort,
                predictPdPort);

        assertThatThrownBy(() -> useCase.calculateDraft("REQ-1", new FormChanges(Map.of())))
                .isInstanceOf(ScoringNotFoundException.class);
    }

    @Test
    @DisplayName("Given valid inputs when calculating draft then return calculated simulation")
    void givenValidInputs_whenCalculatingDraft_thenReturnSimulationDraft() {
        FetchRequestPort fetchRequestPort = requestId -> Map.of(
                "annualIncome", 45000.0,
                "interestRate", 3.5,
                "termMonths", 240,
                "requestedAmount", 150000.0);
        FetchScoringPort fetchScoringPort = requestId -> new BaseScoringData(baseMetrics(), Map.of());
        PredictPdPort predictPdPort = (payload, requestId) -> 0.012;

        CalculateSimulationDraftUseCase useCase = new CalculateSimulationDraftUseCase(
                fetchRequestPort,
                fetchScoringPort,
                predictPdPort);

        SimulationDraft result = useCase.calculateDraft("REQ-1", new FormChanges(Map.of("interestRate", 3.2)));

        assertThat(result).isNotNull();
        assertThat(result.getSimulatedResults().getPd()).isEqualTo(0.012);
        assertThat(result.getSimulatedResults().getMonthlyPayment()).isGreaterThan(0.0);
        assertThat(result.getDelta().getRiskGradeChange()).contains("->");
    }

    private SimulationMetrics baseMetrics() {
        SimulationMetrics metrics = new SimulationMetrics();
        metrics.setPd(0.017);
        metrics.setLgd(0.45);
        metrics.setEad(150000.0);
        metrics.setEcl(1150.0);
        metrics.setRiskGrade("B");
        return metrics;
    }
}
