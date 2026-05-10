package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.SaveSimulationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.FormChanges;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.Simulation;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.SimulationDelta;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.SimulationMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions.DuplicateSimulationNameException;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.ports.output.SimulationRepositoryPort;

@DisplayName("SaveSimulationUseCase")
class SaveSimulationUseCaseTest {

    @Test
    @DisplayName("Given blank scenario name when saving then auto name is assigned")
    void givenBlankScenarioName_whenSaving_thenAutoNameAssigned() {
        final SimulationRepositoryPort repositoryPort = Mockito.mock(SimulationRepositoryPort.class);
        final SaveSimulationUseCase useCase = new SaveSimulationUseCase(repositoryPort);

        when(repositoryPort.existsScenarioName(eq("REQ-2"), any())).thenReturn(false);
        when(repositoryPort.saveSimulation(any())).thenAnswer(invocation -> invocation.getArgument(0));

        final SaveSimulationRequest request = new SaveSimulationRequest();
        request.setRequestId("REQ-2");
        request.setBaseScoringsId("SCO-2");
        request.setScenarioName(" ");
        request.setFormChanges(new FormChanges());
        request.setSimulatedResults(new SimulationMetrics());
        request.setDelta(new SimulationDelta());

        final Simulation result = useCase.saveSimulation(request);

        assertThat(result.getScenarioName()).startsWith("Simulación ");
    }

    @Test
    @DisplayName("Given duplicated scenario name when saving then throw exception")
    void givenDuplicatedScenarioName_whenSaving_thenThrowException() {
        final SimulationRepositoryPort repositoryPort = Mockito.mock(SimulationRepositoryPort.class);
        final SaveSimulationUseCase useCase = new SaveSimulationUseCase(repositoryPort);

        when(repositoryPort.existsScenarioName("REQ-3", "Escenario A")).thenReturn(true);

        final SaveSimulationRequest request = new SaveSimulationRequest();
        request.setRequestId("REQ-3");
        request.setBaseScoringsId("SCO-3");
        request.setScenarioName("Escenario A");
        request.setFormChanges(new FormChanges());
        request.setSimulatedResults(new SimulationMetrics());
        request.setDelta(new SimulationDelta());

        assertThatThrownBy(() -> useCase.saveSimulation(request)).isInstanceOf(DuplicateSimulationNameException.class);
    }
}
