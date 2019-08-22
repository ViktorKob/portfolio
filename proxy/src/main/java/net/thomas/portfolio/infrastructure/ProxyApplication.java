package net.thomas.portfolio.infrastructure;

import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.ProxyServiceProperties.loadProxyServiceConfigurationIntoProperties;
import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import com.github.mthizo247.cloud.netflix.zuul.web.socket.EnableZuulWebSocket;

@SpringBootApplication
@EnableAutoConfiguration
@EnableZuulProxy
@EnableEurekaClient
@EnableZuulWebSocket
@EnableWebSocketMessageBroker
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class ProxyApplication {
	@Configuration
	static class CsrfBugWorkaround extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable().authorizeRequests().antMatchers("/Proxy/**").authenticated().and().httpBasic();
		}
	}

	public static void main(String[] args) {
		loadServicePathsIntoProperties();
		loadProxyServiceConfigurationIntoProperties();
		run(ProxyApplication.class);
	}
}