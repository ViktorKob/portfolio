package net.thomas.portfolio.nexus;

import static java.lang.System.setProperty;
import static net.thomas.portfolio.services.ServiceGlobals.NEXUS_SERVICE_PATH;
import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableEurekaClient
@EnableHystrix
public class NexusServiceApplication {
	@Configuration
	static class CsrfBugWorkaround extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf()
				.disable()
				.authorizeRequests()
				.anyRequest()
				.authenticated()
				.and()
				.httpBasic();
		}
	}

	public static void main(String[] args) {
		setProperty("management.endpoints.web.base-path", NEXUS_SERVICE_PATH + "/actuator");
		setProperty("management.endpoints.jmx.exposure.include", "*");
		run(NexusServiceApplication.class, args);
	}
}