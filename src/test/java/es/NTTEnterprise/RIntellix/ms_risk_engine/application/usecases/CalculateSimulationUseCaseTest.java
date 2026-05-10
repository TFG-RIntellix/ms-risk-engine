package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CalculateSimulationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output.CalculateSimulationResponse;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.ModelPredictionResult;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Scoring;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.SimulationRepositoryPort;

@DisplayName("CalculateSimulationUseCase")
class CalculateSimulationUseCaseTest {

    @Test
    @DisplayName("Given valid request when calculating simulation then merge changes and return calculated metrics")
    void givenValidRequest_whenCalculatingSimulation_thenMergeChangesAndReturnCalculatedMetrics() {
        final SimulationRepositoryPort repositoryPort = Mockito.mock(SimulationRepositoryPort.class);
        final ScoringModelInvocationService modelInvocationService = Mockito.mock(ScoringModelInvocationService.class);

        final CalculateSimulationUseCase useCase = new CalculateSimulationUseCase(
                repositoryPort,
                modelInvocationService,
                "/api/v1/risk/predict-loan");

        final Scoring baseScoring = new Scoring();
        baseScoring.setInputSnapshot(Map.of(
                "monto_prestamo", 120000.0,
                "plazo_meses", 180,
                "tasa_interes", 2.5,
                "ingresos_anuales", 42000.0,
                "situacion_laboral", "Indefinido",
                "tiene_hipoteca", "No"));
        baseScoring.setResults(new RiskMetrics(0.021, 0.70, 120000.0, 1764.0, "B"));

        when(repositoryPort.fetchBaseScoringByRequestId("REQ-1")).thenReturn(baseScoring);
        when(modelInvocationService.invokePrediction(any(), eq("REQ-1"), eq("/api/v1/risk/predict-loan")))
                .thenReturn(CompletableFuture.completedFuture(new ModelPredictionResult(0.012, null, null, null)));

        final CalculateSimulationRequest request = new CalculateSimulationRequest();
        request.setRequestId("REQ-1");
        request.setFormChanges(new FormChanges(3.5, 240, 150000.0, 45000.0, "Indefinido", true));

        final CalculateSimulationResponse response = useCase.calculateSimulation(request);

        final ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(modelInvocationService).invokePrediction(payloadCaptor.capture(), eq("REQ-1"), eq("/api/v1/risk/predict-loan"));

        final Map<String, Object> payload = payloadCaptor.getValue();
        assertThat(payload.get("monto_prestamo")).isEqualTo(150000.0);
        assertThat(payload.get("plazo_meses")).isEqualTo(240);
        assertThat(payload.get("tasa_interes")).isEqualTo(3.5);
        assertThat(payload.get("ingresos_anuales")).isEqualTo(45000.0);
        assertThat(payload.get("tiene_hipoteca")).isEqualTo("Si");

        assertThat(response.getSimulatedResults()).isNotNull();
        assertThat(response.getSimulatedResults().getPd()).isEqualTo(0.012);
        assertThat(response.getSimulatedResults().getMonthlyPayment()).isPositive();
        assertThat(response.getDelta().getPdChange()).isEqualTo(-0.009000000000000001);
        assertThat(response.getDelta().getRiskGradeChange()).isNotBlank();
    }
}
