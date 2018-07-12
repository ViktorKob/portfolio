package net.thomas.portfolio.legal.service;

import static java.util.Arrays.asList;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.CERTAIN;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.POSSIBLY;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.UNLIKELY;
import static net.thomas.portfolio.shared_objects.legal.Legality.LEGAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

import net.thomas.portfolio.legal.system.LegalInfoBuilder;
import net.thomas.portfolio.service_commons.services.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.LegalAdaptorImpl;
import net.thomas.portfolio.service_testing.TestCommunicationWiringTool;
import net.thomas.portfolio.shared_objects.adaptors.AnalyticsAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.Legality;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = { "server.name=legal-service", "server.port:18350", "eureka.client.registerWithEureka:false",
		"eureka.client.fetchRegistry:false" })
public class LegalServiceControllerServiceAdaptorTest {
	private static final TestCommunicationWiringTool COMMUNICATION_WIRING = new TestCommunicationWiringTool("legal-service", 18350);

	private static final DataTypeId SELECTOR_ID = new DataTypeId("TYPE", "FF");

	@TestConfiguration
	static class ServiceMocksSetup {
		@Bean
		public HbaseIndexModelAdaptor getHbaseModelAdaptor() {
			final HbaseIndexModelAdaptorImpl adaptor = mock(HbaseIndexModelAdaptorImpl.class);
			when(adaptor.getSelectorTypes()).thenReturn(asList(SELECTOR_ID.type));
			return adaptor;
		}

		@Bean
		public AnalyticsAdaptorImpl getAnalyticsAdaptor() {
			return mock(AnalyticsAdaptorImpl.class);
		}
	}

	@Autowired
	private AnalyticsAdaptor analyticsAdaptor;
	@Autowired
	private RestTemplate restTemplate;
	private LegalInfoBuilder legalInfoBuilder;
	private LegalAdaptorImpl legalAdaptor;

	@Before
	public void setupController() {
		legalInfoBuilder = new LegalInfoBuilder();
		COMMUNICATION_WIRING.setRestTemplate(restTemplate);
		legalAdaptor = new LegalAdaptorImpl();
		legalAdaptor.initialize(COMMUNICATION_WIRING.setupMockAndGetHttpClient());
	}

	@Test
	public void searchingForUnrestrictedSelectorWithValidUserIsLegal() {
		legalInfoBuilder.setValidUser();
		setupAnalyticsServiceToRespondSelectorIsUnrestricted();
		final Legality legality = legalAdaptor.checkLegalityOfSelectorQuery(SELECTOR_ID, legalInfoBuilder.build());
		assertEquals(LEGAL, legality);
	}

	@Test
	public void searchingForSemiRestrictedSelectorWithValidUserIsLegal() {
		legalInfoBuilder.setValidUser();
		setupAnalyticsServiceToRespondSelectorIsPartiallyRestricted();
		final Legality legality = legalAdaptor.checkLegalityOfSelectorQuery(SELECTOR_ID, legalInfoBuilder.build());
		assertEquals(LEGAL, legality);
	}

	@Test
	public void searchingForRestrictedSelectorWithJustificationIsLegal() {
		legalInfoBuilder.setValidJustification();
		setupAnalyticsServiceToRespondSelectorIsRestricted();
		final Legality legality = legalAdaptor.checkLegalityOfSelectorQuery(SELECTOR_ID, legalInfoBuilder.build());
		assertEquals(LEGAL, legality);
	}

	@Test
	public void shouldReturnOkAfterAuditLoggingInvertedIndexLookup() {
		final Boolean loggingWasSuccessfull = legalAdaptor.auditLogInvertedIndexLookup(SELECTOR_ID, legalInfoBuilder.build());
		assertTrue(loggingWasSuccessfull);
	}

	@Test
	public void shouldReturnOkAfterAuditLoggingStatisticsLookup() {
		final Boolean loggingWasSuccessfull = legalAdaptor.auditLogStatisticsLookup(SELECTOR_ID, legalInfoBuilder.build());
		assertTrue(loggingWasSuccessfull);
	}

	private void setupAnalyticsServiceToRespondSelectorIsUnrestricted() {
		when(analyticsAdaptor.getKnowledge(eq(SELECTOR_ID))).thenReturn(new AnalyticalKnowledge(null, UNLIKELY, UNLIKELY));
	}

	private void setupAnalyticsServiceToRespondSelectorIsPartiallyRestricted() {
		when(analyticsAdaptor.getKnowledge(eq(SELECTOR_ID))).thenReturn(new AnalyticalKnowledge(null, UNLIKELY, POSSIBLY));
	}

	private void setupAnalyticsServiceToRespondSelectorIsRestricted() {
		when(analyticsAdaptor.getKnowledge(eq(SELECTOR_ID))).thenReturn(new AnalyticalKnowledge(null, UNLIKELY, CERTAIN));
	}

}