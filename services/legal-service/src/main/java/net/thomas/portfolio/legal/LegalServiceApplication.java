package net.thomas.portfolio.legal;

import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceProperties.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.LegalServiceProperties.loadLegalConfigurationIntoProperties;
import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = false)
public class LegalServiceApplication {
	@Configuration
	@ComponentScan(basePackages = "net.thomas.portfolio.service_commons.adaptors")
	public class SentinelLoader {
	}

	public static void main(String[] args) {
		loadServicePathsIntoProperties();
		loadDefaultServiceConfigurationIntoProperties();
		loadLegalConfigurationIntoProperties();
		run(LegalServiceApplication.class, args);
	}
}