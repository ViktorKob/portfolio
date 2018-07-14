package net.thomas.portfolio.analytics;

import static java.lang.System.setProperty;
import static net.thomas.portfolio.services.ServiceGlobals.ANALYTICS_SERVICE_PATH;
import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class AnalyticsServiceApplication {
	public static void main(String[] args) {
		setProperty("server.servlet.context-path", ANALYTICS_SERVICE_PATH);
		run(AnalyticsServiceApplication.class, args);
	}
}