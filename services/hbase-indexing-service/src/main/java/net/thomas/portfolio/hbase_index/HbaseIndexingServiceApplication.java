package net.thomas.portfolio.hbase_index;

import static java.lang.System.setProperty;
import static net.thomas.portfolio.services.ServiceGlobals.HBASE_INDEXING_SERVICE_PATH;
import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableEurekaClient
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class HbaseIndexingServiceApplication {
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
		setProperty("server.servlet.context-path", HBASE_INDEXING_SERVICE_PATH);
		run(HbaseIndexingServiceApplication.class, args);
	}
}