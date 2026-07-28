package es.NTTEnterprise.RIntellix.ms_risk_engine.infrastructure.adapters.output.clients;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "msModelClient", url = "${risk.model.base-url:http://localhost:8000}")
/**
 * Core component: MsModelClient.
 * Encapsulates the logic and responsibilities assigned to this element
 * within the Hexagonal Architecture, ensuring separation of concerns.
 *
 * @author Lucía Fernández Mancebo
 * @date 28/07/2026
 */
public interface MsModelClient {

    @PostMapping("${risk.model.predict-loan-path:/api/v1/risk/predict-loan}")
    Map<String, Object> predictPd(@RequestBody Map<String, Object> payload);
}
