package net.thomas.portfolio.analytics.service;

import static java.util.Arrays.asList;
import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.AnalyticsServiceProperties.loadAnalyticsConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceParameters.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.CERTAIN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.service_commons.adaptors.impl.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_testing.TestCommunicationWiringTool;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = { "server.port:18300", "eureka.client.registerWithEureka:false",
		"eureka.client.fetchRegistry:false" })
public class AnalyticsServiceControllerServiceAdaptorTest {
	private static final TestCommunicationWiringTool COMMUNICATION_WIRING = new TestCommunicationWiringTool("analytics-service", 18300);

	@BeforeClass
	public static void setupContextPath() {
		loadAnalyticsConfigurationIntoProperties();
		loadDefaultServiceConfigurationIntoProperties();
		loadServicePathsIntoProperties();
	}

	@TestConfiguration
	static class HbaseServiceMockSetup {

		@Bean(name = "HbaseIndexModelAdaptor")
		public HbaseIndexModelAdaptor getHbaseModelAdaptor() {
			final HbaseIndexModelAdaptor adaptor = mock(HbaseIndexModelAdaptorImpl.class);
			when(adaptor.getDataTypes()).thenReturn(asList(SELECTOR_TYPE));
			return adaptor;
		}
	}

	@Autowired
	private RestTemplate restTemplate;
	private Adaptors adaptors;

	@Before
	public void setUpController() {
		COMMUNICATION_WIRING.setRestTemplate(restTemplate);
		final AnalyticsAdaptorImpl analyticsAdaptor = new AnalyticsAdaptorImpl();
		analyticsAdaptor.initialize(COMMUNICATION_WIRING.setupMockAndGetHttpClient());
		adaptors = new Adaptors.Builder().setAnalyticsAdaptor(analyticsAdaptor)
			.build();
	}

	@Test
	public void shouldReturnFakeKnowledgeUsingEndpoint() {
		final AnalyticalKnowledge knowledge = adaptors.getKnowledge(SELECTOR_ID);
		assertEquals(CERTAIN, knowledge.isKnown);
		assertEquals(CERTAIN, knowledge.isRestricted);
		assertEquals("Target " + KNOWN_RESTRICTED_UID_WITH_ALIAS, knowledge.alias);
	}

	private static final String SELECTOR_TYPE = "TYPE";
	private static final String KNOWN_RESTRICTED_UID_WITH_ALIAS = "FFABCD";
	private static final DataTypeId SELECTOR_ID = new DataTypeId(SELECTOR_TYPE, KNOWN_RESTRICTED_UID_WITH_ALIAS);
}