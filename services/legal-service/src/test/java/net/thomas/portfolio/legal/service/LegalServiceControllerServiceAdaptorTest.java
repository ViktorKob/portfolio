package net.thomas.portfolio.legal.service;

import static java.util.Arrays.asList;
import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceParameters.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.LegalServiceProperties.loadLegalConfigurationIntoProperties;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.CERTAIN;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.UNLIKELY;
import static net.thomas.portfolio.shared_objects.legal.Legality.LEGAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import net.thomas.portfolio.legal.system.LegalInfoForTestBuilder;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.service_commons.adaptors.impl.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.LegalAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.specific.AnalyticsAdaptor;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_testing.TestCommunicationWiringTool;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.Legality;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = { "server.port:18350", "eureka.client.registerWithEureka:false",
		"eureka.client.fetchRegistry:false" })
public class LegalServiceControllerServiceAdaptorTest {
	private static final TestCommunicationWiringTool COMMUNICATION_WIRING = new TestCommunicationWiringTool("legal-service", 18350);

	@BeforeClass
	public static void setupContextPath() {
		loadLegalConfigurationIntoProperties();
		loadDefaultServiceConfigurationIntoProperties();
		loadServicePathsIntoProperties();
	}

	@TestConfiguration
	static class ServiceMocksSetup {
		@Bean(name = "HbaseIndexModelAdaptor")
		public HbaseIndexModelAdaptor getHbaseModelAdaptor() {
			final HbaseIndexModelAdaptor adaptor = mock(HbaseIndexModelAdaptorImpl.class);
			when(adaptor.getSelectorTypes()).thenReturn(asList(SOME_SELECTOR_ID.type));
			return adaptor;
		}
	}

	@MockBean(name = "AnalyticsAdaptor", classes = { AnalyticsAdaptorImpl.class })
	private AnalyticsAdaptor analyticsAdaptor;
	@Autowired
	private RestTemplate restTemplate;
	private LegalInfoForTestBuilder legalInfoBuilder;
	private Adaptors adaptors;

	@Before
	public void setupController() {
		legalInfoBuilder = new LegalInfoForTestBuilder();
		COMMUNICATION_WIRING.setRestTemplate(restTemplate);
		final LegalAdaptorImpl legalAdaptor = new LegalAdaptorImpl();
		legalAdaptor.initialize(COMMUNICATION_WIRING.setupMockAndGetHttpClient());
		adaptors = new Adaptors.Builder().setLegalAdaptor(legalAdaptor)
			.build();
	}

	@Test
	public void shouldVerifyLegalityOfInvertedIndexLookupForValidUserWithJustification() {
		legalInfoBuilder.setValidJustification();
		setupAnalyticsServiceToRespondSelectorIsRestricted();
		final Legality legality = adaptors.checkLegalityOfSelectorQuery(SOME_SELECTOR_ID, legalInfoBuilder.build());
		assertEquals(LEGAL, legality);
	}

	@Test
	public void shouldVerifyLegalityOfStatisticsLookupForValidUserWithJustification() {
		legalInfoBuilder.setValidJustification();
		setupAnalyticsServiceToRespondSelectorIsRestricted();
		final Legality legality = adaptors.checkLegalityOfStatisticsLookup(SOME_SELECTOR_ID, legalInfoBuilder.build());
		assertEquals(LEGAL, legality);
	}

	@Test
	public void shouldReturnOkAfterAuditLoggingInvertedIndexLookup() {
		final Boolean loggingWasSuccessful = adaptors.auditLogInvertedIndexLookup(SOME_SELECTOR_ID, legalInfoBuilder.build());
		assertTrue(loggingWasSuccessful);
	}

	@Test
	public void shouldReturnOkAfterAuditLoggingStatisticsLookup() {
		final Boolean loggingWasSuccessful = adaptors.auditLogStatisticsLookup(SOME_SELECTOR_ID, legalInfoBuilder.build());
		assertTrue(loggingWasSuccessful);
	}

	private void setupAnalyticsServiceToRespondSelectorIsRestricted() {
		when(analyticsAdaptor.getKnowledge(eq(SOME_SELECTOR_ID))).thenReturn(new AnalyticalKnowledge(null, UNLIKELY, CERTAIN));
	}

	private static final DataTypeId SOME_SELECTOR_ID = new DataTypeId("TYPE", "FF");
}