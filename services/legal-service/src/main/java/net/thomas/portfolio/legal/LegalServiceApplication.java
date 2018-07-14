package net.thomas.portfolio.legal;

import static java.lang.System.setProperty;
import static net.thomas.portfolio.services.ServiceGlobals.LEGAL_SERVICE_PATH;
import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrix
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = false)
public class LegalServiceApplication {
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
		setProperty("server.servlet.context-path", LEGAL_SERVICE_PATH);
		run(LegalServiceApplication.class, args);
	}
}