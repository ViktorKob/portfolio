package net.thomas.portfolio.infrastructure;

import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.ProxyServiceProperties.loadProxyServiceConfigurationIntoProperties;
import static org.springframework.boot.SpringApplication.run;
import static org.springframework.http.ResponseEntity.ok;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import com.github.mthizo247.cloud.netflix.zuul.web.socket.EnableZuulWebSocket;

import net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlSuffixBuilder;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;

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

	@RestController
	public class PortfolioIndex {
		@Value("${global-url-prefix}")
		private String globalUrlPrefix;

		private PortfolioHateoasWrappingHelper helper;

		@PostConstruct
		public void initializeService() {
			helper = new PortfolioHateoasWrappingHelper(new UrlFactory(() -> {
				return globalUrlPrefix;
			}, new PortfolioUrlSuffixBuilder()));
		}

		@RequestMapping(path = "/portfolio", produces = "application/hal+json")
		public ResponseEntity<?> getPortfolioRoot() {
			return ok(helper.wrapWithRootLinks("HATEOAS index"));
		}
	}

	public static void main(String[] args) {
		loadServicePathsIntoProperties();
		loadProxyServiceConfigurationIntoProperties();
		run(ProxyApplication.class);
	}
}