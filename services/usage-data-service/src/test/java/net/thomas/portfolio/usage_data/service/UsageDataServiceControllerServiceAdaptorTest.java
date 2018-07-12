package net.thomas.portfolio.usage_data.service;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static net.thomas.portfolio.shared_objects.usage_data.UsageActivityType.READ_DOCUMENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.UsageAdaptorImpl;
import net.thomas.portfolio.service_testing.TestCommunicationWiringTool;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;
import net.thomas.portfolio.usage_data.sql.SqlProxy;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = { "server.name=usage-data-service", "server.port:18200", "eureka.client.registerWithEureka:false",
		"eureka.client.fetchRegistry:false" })
public class UsageDataServiceControllerServiceAdaptorTest {
	private static final TestCommunicationWiringTool COMMUNICATION_WIRING = new TestCommunicationWiringTool("usage-data-service", 18200);

	private static final String DOCUMENT_TYPE = "TYPE";
	private static final String DOCUMENT_UID = "FFABCD";
	private static final DataTypeId DOCUMENT_ID = new DataTypeId(DOCUMENT_TYPE, DOCUMENT_UID);
	private static final String USER = "TEST_USER";
	private static final Long TIME_OF_ACTIVITY = nowInMillisecondsWithSecondsPrecision();
	private static final UsageActivity DEFAULT_ACTIVITY = new UsageActivity(USER, READ_DOCUMENT, TIME_OF_ACTIVITY);
	private static final long AROUND_A_THOUSAND_YEARS_AGO = -1000l * 60 * 60 * 24 * 365 * 1000;
	private static final long AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW = 1000l * 60 * 60 * 24 * 365 * 8000;
	private static final Bounds EVERYTHING = new Bounds(0, MAX_VALUE, AROUND_A_THOUSAND_YEARS_AGO, AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW);
	private static final Bounds DEFAULT_BOUNDS = new Bounds(0, 20, AROUND_A_THOUSAND_YEARS_AGO, AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW);

	private static long nowInMillisecondsWithSecondsPrecision() {
		return currentTimeMillis() / 1000 * 1000;
	}

	@TestConfiguration
	static class HbaseServiceMockSetup {
		@Bean
		public HbaseIndexModelAdaptorImpl getHbaseModelAdaptor() {
			final HbaseIndexModelAdaptorImpl adaptor = mock(HbaseIndexModelAdaptorImpl.class);
			when(adaptor.getDocumentTypes()).thenReturn(asList(DOCUMENT_TYPE));
			return adaptor;
		}

		@Bean
		public SqlProxy getSqlProxy() {
			return mock(SqlProxy.class);
		}
	}

	@Autowired
	private SqlProxy sqlProxy;
	@Autowired
	private RestTemplate restTemplate;
	private UsageAdaptorImpl usageAdaptor;

	@Before
	public void setUpController() throws Exception {
		reset(sqlProxy);
		COMMUNICATION_WIRING.setRestTemplate(restTemplate);
		usageAdaptor = new UsageAdaptorImpl();
		usageAdaptor.initialize(COMMUNICATION_WIRING.setupMockAndGetHttpClient());
	}

	@Test
	public void shouldStoreUsageActivityUsingSqlProxy() {
		usageAdaptor.storeUsageActivity(DOCUMENT_ID, DEFAULT_ACTIVITY);
		verify(sqlProxy, times(1)).storeUsageActivity(eq(DOCUMENT_ID), eq(DEFAULT_ACTIVITY));
	}

	@Test
	public void shouldRespondWithStoredUsageActivity() {
		final UsageActivity responseActivity = usageAdaptor.storeUsageActivity(DOCUMENT_ID, DEFAULT_ACTIVITY);
		assertEquals(DEFAULT_ACTIVITY, responseActivity);
	}

	@Test
	public void shouldFetchActivitiesUsingSqlProxy() {
		when(sqlProxy.fetchUsageActivities(eq(DOCUMENT_ID), eq(EVERYTHING))).thenReturn(singletonList(DEFAULT_ACTIVITY));
		final List<UsageActivity> activities = usageAdaptor.fetchUsageActivities(DOCUMENT_ID, EVERYTHING);
		assertEquals(1, activities.size());
		assertEquals(DEFAULT_ACTIVITY, activities.get(0));
	}

	@Test
	public void shouldFixMissingValues() {
		usageAdaptor.fetchUsageActivities(DOCUMENT_ID, new Bounds(null, null, null, null));
		verify(sqlProxy, times(1)).fetchUsageActivities(eq(DOCUMENT_ID), eq(DEFAULT_BOUNDS));
	}

	@Test
	public void shouldFixInvalidDates() {
		usageAdaptor.fetchUsageActivities(DOCUMENT_ID, new Bounds(DEFAULT_BOUNDS.offset, DEFAULT_BOUNDS.limit, Long.MIN_VALUE, Long.MAX_VALUE));
		verify(sqlProxy, times(1)).fetchUsageActivities(eq(DOCUMENT_ID), eq(DEFAULT_BOUNDS));
	}
}