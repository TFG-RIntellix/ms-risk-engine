package es.NTTEnterprise.RIntellix.ms_risk_engine.infraestructure.adapters.output.clients;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msCoreDataClient", url = "${ms-core-data.base-url:http://localhost:8081}")
public interface MsCoreDataClient {

    @GetMapping("/api/v1/requests/{requestId}")
    Map<String, Object> getRequestById(@PathVariable("requestId") String requestId);

    @GetMapping("/api/v1/scorings/by-request/{requestId}")
    Map<String, Object> getScoringByRequestId(@PathVariable("requestId") String requestId);
}
