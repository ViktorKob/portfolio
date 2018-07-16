package net.thomas.portfolio.analytics;

import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class AnalyticsServiceApplication {
	public static void main(String[] args) {
		loadServicePathsIntoProperties();
		run(AnalyticsServiceApplication.class, args);
	}
}