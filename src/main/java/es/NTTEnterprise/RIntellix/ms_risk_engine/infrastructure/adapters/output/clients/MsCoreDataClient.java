package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.output.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.ScoringDTO;

/**
 * Feign client for ms-core-data scoring endpoints.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-08-2026
 */
@FeignClient(name = "msCoreDataClient", url = "${ms-core-data.base-url:http://localhost:8081}")
public interface MsCoreDataClient {

    @GetMapping("/api/requests/{requestId}/scoring")
    ResponseEntity<ScoringDTO> getScoringByRequestId(@PathVariable("requestId") String requestId);
}
