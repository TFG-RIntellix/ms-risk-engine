package es.NTTEnterprise.RIntellix.ms_risk_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Core component: MsRiskEngineApplication.
 * Encapsulates the logic and responsibilities assigned to this element
 * within the Hexagonal Architecture, ensuring separation of concerns.
 *
 * @author Lucía Fernández Mancebo
 * @date 28/07/2026
 */
@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableScheduling
public class MsRiskEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsRiskEngineApplication.class, args);
	}

}
