package net.thomas.portfolio.usage_data.service;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceParameters.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.UsageDataServiceProperties.loadUsageDataConfigurationIntoProperties;
import static net.thomas.portfolio.shared_objects.usage_data.UsageActivityType.READ_DOCUMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.UsageAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.network.BadRequestException;
import net.thomas.portfolio.service_testing.TestCommunicationWiringTool;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;
import net.thomas.portfolio.usage_data.sql.SqlProxy;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = { "server.port:18200", "eureka.client.registerWithEureka:false",
		"eureka.client.fetchRegistry:false" })
public class UsageDataServiceControllerServiceAdaptorTest {
	private static final TestCommunicationWiringTool COMMUNICATION_WIRING = new TestCommunicationWiringTool("usage-data-service", 18200);

	@BeforeClass
	public static void setupContextPath() {
		loadUsageDataConfigurationIntoProperties();
		loadDefaultServiceConfigurationIntoProperties();
		loadServicePathsIntoProperties();
	}

	@TestConfiguration
	static class HbaseServiceMockSetup {
		@Bean(name = "HbaseIndexModelAdaptor")
		public HbaseIndexModelAdaptor getHbaseModelAdaptor() {
			final HbaseIndexModelAdaptor adaptor = mock(HbaseIndexModelAdaptorImpl.class);
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
	private Adaptors adaptors;

	@Before
	public void setUpController() throws Exception {
		reset(sqlProxy);
		COMMUNICATION_WIRING.setRestTemplate(restTemplate);
		final UsageAdaptorImpl usageAdaptor = new UsageAdaptorImpl();
		usageAdaptor.initialize(COMMUNICATION_WIRING.setupMockAndGetHttpClient());
		adaptors = new Adaptors.Builder().setUsageAdaptor(usageAdaptor)
			.build();
	}

	@Test
	public void shouldStoreUsageActivityUsingSqlProxy() {
		adaptors.storeUsageActivity(DOCUMENT_ID, DEFAULT_ACTIVITY);
		verify(sqlProxy, times(1)).storeUsageActivity(eq(DOCUMENT_ID), eq(DEFAULT_ACTIVITY));
	}

	@Test
	public void shouldRespondWithStoredUsageActivity() {
		final UsageActivity responseActivity = adaptors.storeUsageActivity(DOCUMENT_ID, DEFAULT_ACTIVITY);
		assertEquals(DEFAULT_ACTIVITY, responseActivity);
	}

	@Test
	public void shouldFetchActivitiesUsingSqlProxy() {
		when(sqlProxy.fetchUsageActivities(eq(DOCUMENT_ID), eq(EVERYTHING))).thenReturn(new UsageActivities(singletonList(DEFAULT_ACTIVITY)));
		final UsageActivities activities = adaptors.fetchUsageActivities(DOCUMENT_ID, EVERYTHING);
		assertTrue(activities.hasData());
		assertEquals(DEFAULT_ACTIVITY, activities.get(0));
	}

	@Test
	public void shouldFixMissingValues() {
		adaptors.fetchUsageActivities(DOCUMENT_ID, new Bounds(null, null, null, null));
		verify(sqlProxy, times(1)).fetchUsageActivities(eq(DOCUMENT_ID), eq(DEFAULT_BOUNDS));
	}

	@Test
	public void shouldFixInvalidDates() {
		adaptors.fetchUsageActivities(DOCUMENT_ID, new Bounds(DEFAULT_BOUNDS.offset, DEFAULT_BOUNDS.limit, Long.MIN_VALUE, Long.MAX_VALUE));
		verify(sqlProxy, times(1)).fetchUsageActivities(eq(DOCUMENT_ID), eq(DEFAULT_BOUNDS));
	}

	@Test(expected = BadRequestException.class)
	public void shouldThrowExceptionWhenStoringUsageActivityUsingInvalidType() {
		adaptors.storeUsageActivity(DOCUMENT_ID_WITH_INVALID_TYPE, DEFAULT_ACTIVITY);
	}

	@Test(expected = BadRequestException.class)
	public void shouldThrowExceptionWhenStoringUsageActivityUsingInvalidUid() {
		adaptors.storeUsageActivity(DOCUMENT_ID_WITH_INVALID_UID, DEFAULT_ACTIVITY);
	}

	@Test(expected = BadRequestException.class)
	public void shouldThrowExceptionWhenStoringUsageActivityUsingInvalidUsername() {
		adaptors.storeUsageActivity(DOCUMENT_ID, new UsageActivity(null, READ_DOCUMENT, TIME_OF_ACTIVITY));
	}

	@Test(expected = BadRequestException.class)
	public void shouldThrowExceptionWhenStoringUsageActivityUsingInvalidActivityType() {
		adaptors.storeUsageActivity(DOCUMENT_ID, new UsageActivity(USER, null, TIME_OF_ACTIVITY));
	}

	@Test(expected = BadRequestException.class)
	public void shouldThrowExceptionWhenFetchingUsageActivitiesUsingInvalidType() {
		adaptors.fetchUsageActivities(DOCUMENT_ID_WITH_INVALID_TYPE, DEFAULT_BOUNDS);
	}

	@Test(expected = BadRequestException.class)
	public void shouldThrowExceptionWhenFetchingUsageActivitiesUsingInvalidUid() {
		adaptors.fetchUsageActivities(DOCUMENT_ID_WITH_INVALID_UID, DEFAULT_BOUNDS);
	}

	@Test(expected = BadRequestException.class)
	public void shouldThrowExceptionWhenFetchingUsageActivitiesUsingInvalidOffset() {
		adaptors.fetchUsageActivities(DOCUMENT_ID, new Bounds(-1, 20, AROUND_A_THOUSAND_YEARS_AGO, AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW));
	}

	@Test(expected = BadRequestException.class)
	public void shouldThrowExceptionWhenFetchingUsageActivitiesUsingInvalidLimit() {
		adaptors.fetchUsageActivities(DOCUMENT_ID, new Bounds(0, -1, AROUND_A_THOUSAND_YEARS_AGO, AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW));
	}

	private static final String DOCUMENT_TYPE = "TYPE";
	private static final String DOCUMENT_UID = "FFABCD";
	private static final DataTypeId DOCUMENT_ID = new DataTypeId(DOCUMENT_TYPE, DOCUMENT_UID);
	private static final DataTypeId DOCUMENT_ID_WITH_INVALID_TYPE = new DataTypeId("INVALID_TYPE", DOCUMENT_UID);
	private static final DataTypeId DOCUMENT_ID_WITH_INVALID_UID = new DataTypeId(DOCUMENT_TYPE, "F");
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
}