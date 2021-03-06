package net.thomas.portfolio.analytics;

import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.AnalyticsServiceProperties.loadAnalyticsConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceProperties.loadDefaultServiceConfigurationIntoProperties;
import static org.springframework.boot.SpringApplication.run;
import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableDiscoveryClient
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class AnalyticsServiceApplication {
	@Configuration
	@ComponentScan(basePackages = "net.thomas.portfolio.service_commons.adaptors")
	static class CsrfBugWorkaround extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.cors().and().csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic();
		}
	}

	@Configuration
	@EnableSwagger2
	public class SwaggerConfig {
		@Bean
		public Docket api() {
			return new Docket(SWAGGER_2).select().apis(basePackage("net.thomas.portfolio.analytics.service")).paths(any()).build();
		}
	}

	public static void main(String[] args) {
		loadServicePathsIntoProperties();
		loadDefaultServiceConfigurationIntoProperties();
		loadAnalyticsConfigurationIntoProperties();
		run(AnalyticsServiceApplication.class, args);
	}
}