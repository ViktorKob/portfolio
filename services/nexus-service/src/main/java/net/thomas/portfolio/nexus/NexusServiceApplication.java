package net.thomas.portfolio.nexus;

import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceProperties.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.NexusServiceProperties.loadNexusConfigurationIntoProperties;
import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableEurekaClient
public class NexusServiceApplication {
	@Configuration
	@ComponentScan(basePackages = "net.thomas.portfolio.service_commons.adaptors")
	static class CorsBugWorkaround extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic();
		}
	}

	public static void main(String[] args) {
		loadServicePathsIntoProperties();
		loadDefaultServiceConfigurationIntoProperties();
		loadNexusConfigurationIntoProperties();
		run(NexusServiceApplication.class, args);
	}
}