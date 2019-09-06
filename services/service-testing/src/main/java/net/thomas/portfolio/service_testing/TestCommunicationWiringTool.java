package net.thomas.portfolio.service_testing;

import org.springframework.web.client.RestTemplate;

import net.thomas.portfolio.common.services.parameters.Credentials;
import net.thomas.portfolio.common.services.parameters.ServiceDependency;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlSuffixBuilder;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;

public class TestCommunicationWiringTool {
	private final String serviceName;
	private final int port;
	private RestTemplate restTemplate;

	public TestCommunicationWiringTool(String serviceName, int port) {
		this.serviceName = serviceName;
		this.port = port;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public UrlFactory getUrlFactory() {
		final UrlFactory urlFactory = new UrlFactory(() -> {
			return "http://localhost:" + port;
		}, new PortfolioUrlSuffixBuilder());
		return urlFactory;
	}

	public HttpRestClient getHttpRestClient() {
		final ServiceDependency serviceInfo = new ServiceDependency(serviceName, new Credentials("service-user", "password"));
		return new HttpRestClient(restTemplate, serviceInfo);
	}
}