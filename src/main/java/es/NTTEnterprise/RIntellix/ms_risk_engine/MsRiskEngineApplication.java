package es.NTTEnterprise.RIntellix.ms_risk_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class MsRiskEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsRiskEngineApplication.class, args);
	}

}
