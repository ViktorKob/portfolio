package net.thomas.portfolio.analytics.service;

import static java.util.Arrays.asList;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.CERTAIN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.Credentials;
import net.thomas.portfolio.common.services.ServiceDependency;
import net.thomas.portfolio.service_commons.services.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = { "server.name=analytics-service", "server.port:18300", "eureka.client.registerWithEureka:false",
		"eureka.client.fetchRegistry:false" })
public class AnalyticsServiceControllerServiceAdaptorTest {
	private static final String SELECTOR_TYPE = "TYPE";
	private static final String KNOWN_RESTRICTED_UID_WITH_ALIAS = "FFABCD";
	private static final DataTypeId SELECTOR_ID = new DataTypeId(SELECTOR_TYPE, KNOWN_RESTRICTED_UID_WITH_ALIAS);
	private static final String ANALYTICS_SERVICE = "analytics-service";

	@TestConfiguration
	static class HbaseServiceMockSetup {
		@Bean
		public HbaseIndexModelAdaptorImpl getHbaseModelAdaptor() {
			final HbaseIndexModelAdaptorImpl adaptor = mock(HbaseIndexModelAdaptorImpl.class);
			when(adaptor.getDataTypes()).thenReturn(asList(SELECTOR_TYPE));
			return adaptor;
		}
	}

	@Autowired
	private RestTemplate restTemplate;
	private AnalyticsAdaptorImpl analyticsAdaptor;

	@Before
	public void setUpController() {
		final ServiceDependency analyticsServiceConfig = new ServiceDependency(ANALYTICS_SERVICE, new Credentials("service-user", "password"));
		final InstanceInfo analyticsServiceInfoMock = mock(InstanceInfo.class);
		when(analyticsServiceInfoMock.getHomePageUrl()).thenReturn("http://localhost:18300");
		final EurekaClient discoveryClientMock = mock(EurekaClient.class);
		when(discoveryClientMock.getNextServerFromEureka(eq(ANALYTICS_SERVICE), anyBoolean())).thenReturn(analyticsServiceInfoMock);
		analyticsAdaptor = new AnalyticsAdaptorImpl();
		analyticsAdaptor.initialize(new HttpRestClient(discoveryClientMock, restTemplate, analyticsServiceConfig));
	}

	@Test
	public void shouldReturnFakeKnowledgeUsingEndpoint() {
		final AnalyticalKnowledge knowledge = analyticsAdaptor.getKnowledge(SELECTOR_ID);
		assertEquals(CERTAIN, knowledge.isKnown);
		assertEquals(CERTAIN, knowledge.isRestricted);
		assertEquals("Target " + KNOWN_RESTRICTED_UID_WITH_ALIAS, knowledge.alias);
	}
}