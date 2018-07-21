package net.thomas.portfolio.service_testing;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.parameters.Credentials;
import net.thomas.portfolio.common.services.parameters.ServiceDependency;
import net.thomas.portfolio.service_commons.network.HttpRestClient;

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

	public HttpRestClient setupMockAndGetHttpClient() {
		final EurekaClient discoveryClientMock = mockClientDiscovery();
		return buildClientAccess(discoveryClientMock);
	}

	private EurekaClient mockClientDiscovery() {
		final InstanceInfo serviceInfoMock = mock(InstanceInfo.class);
		when(serviceInfoMock.getHomePageUrl()).thenReturn("http://localhost:" + port);
		final EurekaClient discoveryClientMock = mock(EurekaClient.class);
		when(discoveryClientMock.getNextServerFromEureka(eq(serviceName), anyBoolean())).thenReturn(serviceInfoMock);
		return discoveryClientMock;
	}

	private HttpRestClient buildClientAccess(final EurekaClient discoveryClientMock) {
		final ServiceDependency serviceInfo = new ServiceDependency(serviceName, new Credentials("service-user", "password"));
		return new HttpRestClient(discoveryClientMock, restTemplate, serviceInfo);
	}
}