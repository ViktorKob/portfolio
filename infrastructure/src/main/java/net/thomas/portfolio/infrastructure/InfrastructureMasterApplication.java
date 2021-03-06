package net.thomas.portfolio.infrastructure;

import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.InfrastructureServiceProperties.loadInfrastructureServiceConfigurationIntoProperties;
import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableAutoConfiguration
@EnableEurekaServer
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class InfrastructureMasterApplication {
	@Configuration
	static class CsrfBugWorkaround extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic();
		}
	}

	@ConditionalOnMissingBean
	@Bean
	public HttpTraceRepository httpTraceRepository() {
		return new InMemoryHttpTraceRepository();
	}

	public static void main(String[] args) {
		loadServicePathsIntoProperties();
		loadInfrastructureServiceConfigurationIntoProperties();
		run(InfrastructureMasterApplication.class);
	}
}