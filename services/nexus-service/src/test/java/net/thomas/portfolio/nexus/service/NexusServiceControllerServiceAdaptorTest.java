package net.thomas.portfolio.nexus.service;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.reverse;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.joining;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.USER;
import static net.thomas.portfolio.nexus.service.UsageActivityMatcher.matchesActivity;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.COMPLEX_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.DOCUMENT_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.EXAMPLE_IDS;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.RAW_DATA_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SIMPLE_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_DECIMAL;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_DOCUMENT_INFOS;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_FORMATTED_DATE_ONLY_TIMESTAMP;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_FORMATTED_TIMESTAMP;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_GEO_LOCATION;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_HEADLINE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_HTML;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_INTEGER;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_LONG_INTEGER;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_MISSING_UID;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_SIMPLE_REP;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_STRING;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_TIMESTAMP;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_USAGE_ACTIVITIES;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_USAGE_ACTIVITY;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_USER;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.setUpHbaseAdaptorMock;
import static net.thomas.portfolio.services.Service.NEXUS_SERVICE;
import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceParameters.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.NexusServiceProperties.loadNexusConfigurationIntoProperties;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;
import static net.thomas.portfolio.shared_objects.legal.Legality.LEGAL;
import static net.thomas.portfolio.shared_objects.usage_data.UsageActivityType.READ_DOCUMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpMethod.GET;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.nexus.service.test_utils.GraphQlQueryBuilder;
import net.thomas.portfolio.service_commons.adaptors.impl.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.LegalAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.RenderingAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.UsageAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.specific.AnalyticsAdaptor;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.adaptors.specific.LegalAdaptor;
import net.thomas.portfolio.service_commons.adaptors.specific.RenderingAdaptor;
import net.thomas.portfolio.service_commons.adaptors.specific.UsageAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_testing.TestCommunicationWiringTool;
import net.thomas.portfolio.services.ServiceEndpoint;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter;

/***
 * These tests are currently all being kept in the same class to encourage running them before checking in. The class has a startup time of around 7 seconds
 * while each test takes less than 10 ms. Splitting it up would slow down the test suite considerably so I chose speed over separation here.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = { "server.port:18100", "eureka.client.registerWithEureka:false",
		"eureka.client.fetchRegistry:false" })
public class NexusServiceControllerServiceAdaptorTest {
	private static final ServiceEndpoint GRAPHQL = () -> {
		return "/graphql";
	};
	private static final TestCommunicationWiringTool COMMUNICATION_WIRING = new TestCommunicationWiringTool("nexus-service", 18100);
	private static final ParameterizedTypeReference<LinkedHashMap<String, Object>> JSON = new ParameterizedTypeReference<LinkedHashMap<String, Object>>() {
	};

	@TestConfiguration
	static class HbaseServiceMockSetup {
		@Bean(name = "HbaseIndexModelAdaptor")
		public HbaseIndexModelAdaptor getHbaseModelAdaptor() {
			final HbaseIndexModelAdaptor adaptor = mock(HbaseIndexModelAdaptorImpl.class);
			setUpHbaseAdaptorMock(adaptor);
			return adaptor;
		}
	}

	@BeforeClass
	public static void setupContextPath() {
		loadNexusConfigurationIntoProperties();
		loadDefaultServiceConfigurationIntoProperties();
		loadServicePathsIntoProperties();
	}

	@Autowired
	private HbaseIndexModelAdaptor hbaseAdaptor;
	@MockBean(name = "AnalyticsAdaptor", classes = { AnalyticsAdaptorImpl.class })
	private AnalyticsAdaptor analyticsAdaptor;
	@MockBean(name = "LegalAdaptor", classes = { LegalAdaptorImpl.class })
	private LegalAdaptor legalAdaptor;
	@MockBean(name = "RenderAdaptor", classes = { RenderingAdaptorImpl.class })
	private RenderingAdaptor renderingAdaptor;
	@MockBean(name = "UsageAdaptor", classes = { UsageAdaptorImpl.class })
	private UsageAdaptor usageAdaptor;
	@Autowired
	private RestTemplate restTemplate;
	private HttpRestClient httpClient;
	private GraphQlQueryBuilder queryBuilder;

	@Before
	public void setupController() {
		reset(analyticsAdaptor, hbaseAdaptor, legalAdaptor, renderingAdaptor, usageAdaptor);
		setUpHbaseAdaptorMock(hbaseAdaptor);
		COMMUNICATION_WIRING.setRestTemplate(restTemplate);
		httpClient = COMMUNICATION_WIRING.setupMockAndGetHttpClient();
		queryBuilder = new GraphQlQueryBuilder();
	}

	// ***************************************
	// *** SelectorFetcher
	// ***************************************
	@Test
	public void shouldLookupSelectorByUidAndFetchUid() {
		assertUidInSomeEntityOfTypeEqualsUid(SIMPLE_TYPE);
	}

	@Test
	public void shouldLookupSelectorBySimpleRepAndFetchUid() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("simpleRepresentation", SOME_SIMPLE_REP);
		queryBuilder.setSimpleRepToFieldValueQuery(SIMPLE_TYPE, "uid");
		assertEquals(someId.uid, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "uid"));
	}

	@Test
	public void shouldReturnErrorWhenSelectorNotPresent() {
		queryBuilder.setNothingToFieldValueQuery(SIMPLE_TYPE, "uid");
		assertContainsExpectedText("uid or simple representation must be specified",
				executeQueryAndLookupResponseAtPath(queryBuilder.build(), "errors", "message"));
	}

	@Test
	public void shouldLookupSelectorByUidAndFetchRawFormForEntity() throws JsonProcessingException {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "rawData");
		final String expectedAnswer = new ObjectMapper().writeValueAsString(hbaseAdaptor.getDataType(someId));
		assertEquals(expectedAnswer, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "rawData"));
	}

	@Test
	public void shouldLookupSelectorByUidAndReturnNullWhenEntityIsMissing() throws JsonProcessingException {
		queryBuilder.addVariable("uid", SOME_MISSING_UID);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "rawData");
		assertNull(executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "rawData"));
	}

	// ***************************************
	// *** *FieldFetchers
	// ***************************************
	@Test
	public void shouldLookupSelectorUidAndFetchStringField() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "string");
		assertEquals(SOME_STRING, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "string"));
	}

	@Test
	public void shouldLookupSelectorUidAndFetchStringsField() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "strings");
		final List<Object> strings = executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "strings");
		assertEquals(SOME_STRING, strings.get(0));
	}

	@Test
	public void shouldLookupSelectorUidAndFetchIntegerField() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "integer");
		assertEquals(SOME_INTEGER, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "integer"));
	}

	@Test
	public void shouldLookupSelectorUidAndFetchLongIntegerField() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "long");
		assertEquals(SOME_LONG_INTEGER, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "long"));
	}

	@Test
	public void shouldLookupSelectorUidAndFetchDecimalField() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "decimal");
		assertEquals(SOME_DECIMAL, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "decimal"));
	}

	@Test
	public void shouldLookupSelectorUidAndFetchGeoLocationField() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "geoLocation{longitude latitude}");
		final Map<String, Object> response = executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "geoLocation");
		assertEquals(SOME_GEO_LOCATION.longitude, response.get("longitude"));
		assertEquals(SOME_GEO_LOCATION.latitude, response.get("latitude"));
	}

	@Test
	public void shouldLookupSelectorUidAndFetchTimestampField() {
		final String expectedTimestamp = new DateConverter.Iec8601DateConverter().formatTimestamp(SOME_TIMESTAMP.getTimestamp());
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "timestamp");
		assertEquals(expectedTimestamp, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "timestamp"));
	}

	// ***************************************
	// *** SelectorSuggestionsFetcher
	// ***************************************
	@Test
	public void shouldReturnSuggestionBasedOnSimpleRep() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		when(hbaseAdaptor.getSelectorSuggestions(eq(SOME_SIMPLE_REP))).thenReturn(singletonList(someId));
		queryBuilder.addVariable("simpleRepresentation", SOME_SIMPLE_REP);
		queryBuilder.setSuggestionsToSelectorsQuery();
		assertEquals(someId.uid, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", "suggest", "uid"));
	}

	// ***************************************
	// *** DataTypeFetcher
	// ***************************************
	@Test
	public void shouldLookupRawTypeUidAndFetchUid() {
		assertUidInSomeEntityOfTypeEqualsUid(RAW_DATA_TYPE);
	}

	// ***************************************
	// *** DocumentFetcher
	// ***************************************
	@Test
	public void shouldLookupDocumentUidAndFetchUid() {
		assertUidInSomeEntityOfTypeEqualsUid(DOCUMENT_TYPE);
	}

	@Test
	public void shouldLookupDocumentUidAndFetchTimeOfEvent() {
		assertFieldInSomeEntityOfTypeEqualsValue(DOCUMENT_TYPE, SOME_TIMESTAMP.getTimestamp(), "timeOfEvent", "timestamp");
	}

	@Test
	public void shouldLookupDocumentUidAndFetchTimeOfInterception() {
		assertFieldInSomeEntityOfTypeEqualsValue(DOCUMENT_TYPE, SOME_TIMESTAMP.getTimestamp(), "timeOfInterception", "timestamp");
	}

	@Test
	public void shouldLookupDocumentUidAndFetchFormattedTimeOfEvent() {
		assertFieldInSomeEntityOfTypeEqualsValue(DOCUMENT_TYPE, SOME_FORMATTED_TIMESTAMP, "formattedTimeOfEvent");
	}

	@Test
	public void shouldLookupDocumentUidAndFetchFormattedTimeOfInterception() {
		assertFieldInSomeEntityOfTypeEqualsValue(DOCUMENT_TYPE, SOME_FORMATTED_TIMESTAMP, "formattedTimeOfInterception");
	}

	@Test
	public void shouldLookupDocumentUidAndFetchFormattedDateOfEvent() {
		final DataTypeId someId = EXAMPLE_IDS.get(DOCUMENT_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(DOCUMENT_TYPE, "formattedTimeOfEvent(detailLevel:\"dateOnly\")");
		assertEquals(SOME_FORMATTED_DATE_ONLY_TIMESTAMP,
				executeQueryAndLookupResponseAtPath(queryBuilder.build(), queryPath(DOCUMENT_TYPE, "formattedTimeOfEvent")));
	}

	@Test
	public void shouldLookupDocumentUidAndFetchFormattedDateOfInterception() {
		final DataTypeId someId = EXAMPLE_IDS.get(DOCUMENT_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(DOCUMENT_TYPE, "formattedTimeOfInterception(detailLevel:\"dateOnly\")");
		assertEquals(SOME_FORMATTED_DATE_ONLY_TIMESTAMP,
				executeQueryAndLookupResponseAtPath(queryBuilder.build(), queryPath(DOCUMENT_TYPE, "formattedTimeOfInterception")));
	}

	@Test
	public void shouldLookupDocumentUidAndStoreReadDocumentActivityBeforeRenderingText() {
		queryBuilder.addVariable(USER.getName(), SOME_USER);
		queryBuilder.addVariable("uid", EXAMPLE_IDS.get(DOCUMENT_TYPE).uid);
		queryBuilder.setUidAndUserToFieldValueQuery(DOCUMENT_TYPE, "headline");
		executeQueryAndLookupResponseAtPath(queryBuilder.build(), queryPath(DOCUMENT_TYPE, "headline"));
		verify(usageAdaptor, times(1)).storeUsageActivity(eq(EXAMPLE_IDS.get(DOCUMENT_TYPE)), argThat(matchesActivity(SOME_USER, READ_DOCUMENT)));
	}

	@Test
	public void shouldLookupDocumentUidAndStoreReadDocumentActivityWithUnspecifiedUserBeforeRenderingText() {
		executeGraphQlLookupAndIgnoreResponse(DOCUMENT_TYPE, "headline");
		verify(usageAdaptor, times(1)).storeUsageActivity(eq(EXAMPLE_IDS.get(DOCUMENT_TYPE)), argThat(matchesActivity("Unspecified user", READ_DOCUMENT)));
	}

	@Test
	public void shouldLookupDocumentUidAndFetchHeadline() {
		when(renderingAdaptor.renderAsText(eq(EXAMPLE_IDS.get(DOCUMENT_TYPE)))).thenReturn(SOME_HEADLINE);
		assertFieldInSomeEntityOfTypeEqualsValue(DOCUMENT_TYPE, SOME_HEADLINE, "headline");
	}

	@Test
	public void shouldLookupDocumentUidAndStoreReadDocumentActivityBeforeRenderingHtml() {
		queryBuilder.addVariable(USER.getName(), SOME_USER);
		queryBuilder.addVariable("uid", EXAMPLE_IDS.get(DOCUMENT_TYPE).uid);
		queryBuilder.setUidAndUserToFieldValueQuery(DOCUMENT_TYPE, "html");
		executeQueryAndLookupResponseAtPath(queryBuilder.build(), queryPath(DOCUMENT_TYPE, "html"));
		verify(usageAdaptor, times(1)).storeUsageActivity(eq(EXAMPLE_IDS.get(DOCUMENT_TYPE)), argThat(matchesActivity(SOME_USER, READ_DOCUMENT)));
	}

	@Test
	public void shouldLookupDocumentUidAndStoreReadDocumentActivityWithUnspecifiedUserBeforeRenderingHtml() {
		executeGraphQlLookupAndIgnoreResponse(DOCUMENT_TYPE, "html");
		verify(usageAdaptor, times(1)).storeUsageActivity(eq(EXAMPLE_IDS.get(DOCUMENT_TYPE)), argThat(matchesActivity("Unspecified user", READ_DOCUMENT)));
	}

	@Test
	public void shouldLookupDocumentUidAndFetchHtml() {
		when(renderingAdaptor.renderAsHtml(eq(EXAMPLE_IDS.get(DOCUMENT_TYPE)))).thenReturn(SOME_HTML);
		assertFieldInSomeEntityOfTypeEqualsValue(DOCUMENT_TYPE, SOME_HTML, "html");
	}

	// ***************************************
	// *** DocumentListFetcher
	// ***************************************
	@Test
	public void shouldReturnErrorWhenInvertedIndexLookupIsIllegal() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		when(legalAdaptor.checkLegalityOfInvertedIndexLookup(eq(someId), any())).thenReturn(ILLEGAL);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "events{uid}");
		assertContainsExpectedText("must be justified", executeQueryAndLookupResponseAtPath(queryBuilder.build(), "errors", "message"));
	}

	@Test
	public void shouldReturnEmptyListWhenInvertedIndexLookupAuditLoggingFails() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		when(legalAdaptor.checkLegalityOfInvertedIndexLookup(eq(someId), any())).thenReturn(LEGAL);
		when(legalAdaptor.auditLogInvertedIndexLookup(eq(someId), any())).thenReturn(false);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "events{uid}");
		assertIsEmpty(executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "events"));
	}

	@Test
	public void shouldFetchDocumentInfosForSelector() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		when(legalAdaptor.checkLegalityOfInvertedIndexLookup(eq(someId), any())).thenReturn(LEGAL);
		when(legalAdaptor.auditLogInvertedIndexLookup(eq(someId), any())).thenReturn(true);
		when(hbaseAdaptor.lookupSelectorInInvertedIndex(any())).thenReturn(SOME_DOCUMENT_INFOS);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "events{uid}");
		final Object result = executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "events", "uid");
		assertEquals(firstId(SOME_DOCUMENT_INFOS.getInfos()).uid, result);
	}

	// ***************************************
	// *** SelectorStatisticsFetcher
	// ***************************************
	@Test
	public void shouldReturnErrorWhenSelectorStatisticsLookupIsIllegal() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		when(legalAdaptor.checkLegalityOfStatisticsLookup(eq(someId), any())).thenReturn(ILLEGAL);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "statistics{infinityTotal}");
		assertContainsExpectedText("must be justified", executeQueryAndLookupResponseAtPath(queryBuilder.build(), "errors", "message"));
	}

	@Test
	public void shouldReturn0CountsWhenSelectorStatisticsLookupAuditLoggingFails() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		when(legalAdaptor.checkLegalityOfStatisticsLookup(eq(someId), any())).thenReturn(LEGAL);
		when(legalAdaptor.auditLogStatisticsLookup(eq(someId), any())).thenReturn(false);
		when(hbaseAdaptor.getStatistics(any())).thenReturn(new Statistics(singletonMap(INFINITY, 1l)));
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "statistics{infinityTotal}");
		assertEquals(0, (int) executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "statistics", "infinityTotal"));
	}

	@Test
	public void shouldFetchStatisticsForSelector() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		when(legalAdaptor.checkLegalityOfStatisticsLookup(eq(someId), any())).thenReturn(LEGAL);
		when(legalAdaptor.auditLogStatisticsLookup(eq(someId), any())).thenReturn(true);
		when(hbaseAdaptor.getStatistics(any())).thenReturn(new Statistics(singletonMap(INFINITY, 1l)));
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "statistics{infinityTotal}");
		assertEquals(1, (int) executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "statistics", "infinityTotal"));
	}

	// ***************************************
	// *** SubTypeFetcher
	// ***************************************
	@Test
	public void shouldLookupSelectorUidAndFetchSimpleSubType() {
		final DataTypeId complexTypeId = EXAMPLE_IDS.get(COMPLEX_TYPE);
		final DataTypeId simpleTypeId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("uid", complexTypeId.uid);
		queryBuilder.setUidToFieldValueQuery(COMPLEX_TYPE, "simpleType{uid}");
		assertEquals(simpleTypeId.uid, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", COMPLEX_TYPE, "simpleType", "uid"));
	}

	@Test
	public void shouldLookupSelectorUidAndReturnNullWhenSubTypeIsMissing() {
		final DataTypeId complexTypeId = EXAMPLE_IDS.get(COMPLEX_TYPE);
		queryBuilder.addVariable("uid", complexTypeId.uid);
		queryBuilder.setUidToFieldValueQuery(COMPLEX_TYPE, "missingSimpleType{uid}");
		assertNull(executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", COMPLEX_TYPE, "missingSimpleType"));
	}

	// ***************************************
	// *** SubTypeArrayFetcher
	// ***************************************
	@Test
	public void shouldLookupSelectorUidAndFetchSimpleSubTypes() {
		final DataTypeId complexTypeId = EXAMPLE_IDS.get(COMPLEX_TYPE);
		final DataTypeId simpleTypeId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("uid", complexTypeId.uid);
		queryBuilder.setUidToFieldValueQuery(COMPLEX_TYPE, "arraySimpleType{uid}");
		assertEquals(simpleTypeId.uid, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", COMPLEX_TYPE, "arraySimpleType", "uid"));
	}

	@Test
	public void shouldLookupSelectorUidAndReturnEmptyListForMissingField() {
		final DataTypeId complexTypeId = EXAMPLE_IDS.get(COMPLEX_TYPE);
		queryBuilder.addVariable("uid", complexTypeId.uid);
		queryBuilder.setUidToFieldValueQuery(COMPLEX_TYPE, "missingArrayType{uid}");
		assertIsEmpty(executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", COMPLEX_TYPE, "missingArrayType"));
	}

	// ***************************************
	// *** UsageActivitiesFetcher
	// ***************************************
	@Test
	public void shouldFetchUsageActivities() {
		final DataTypeId someId = EXAMPLE_IDS.get(DOCUMENT_TYPE);
		when(usageAdaptor.fetchUsageActivities(eq(someId), any())).thenReturn(SOME_USAGE_ACTIVITIES);
		SOME_USAGE_ACTIVITIES.setActivities(singletonList(SOME_USAGE_ACTIVITY));
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(DOCUMENT_TYPE, "usageActivities{activityType}");
		assertEquals(SOME_USAGE_ACTIVITY.type.name(),
				executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", DOCUMENT_TYPE, "usageActivities", "activityType"));
	}

	// ***************************************
	// *** UsageActivitiesMutator
	// ***************************************
	// @Test
	// public void shouldStoreUsageActivity() {
	// final DataTypeId someId = EXAMPLE_IDS.get(DOCUMENT_TYPE);
	// when(usageAdaptor.storeUsageActivity(eq(someId), eq(SOME_USAGE_ACTIVITY))).thenReturn(SOME_USAGE_ACTIVITY);
	// queryBuilder.addVariable("uid", someId.uid);
	// queryBuilder.setUidToFieldValueQuery(DOCUMENT_TYPE, "usageActivities{activityType}");
	//
	// assertEquals(SOME_USAGE_ACTIVITY.type.name(), executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data",
	// DOCUMENT_TYPE, "usageActivities", "activityType"));
	// }

	private void assertUidInSomeEntityOfTypeEqualsUid(final String dataTypeType) {
		final DataTypeId someId = EXAMPLE_IDS.get(dataTypeType);
		assertFieldInSomeEntityOfTypeEqualsValue(dataTypeType, someId.uid, "uid");
	}

	private <T> void executeGraphQlLookupAndIgnoreResponse(final String dataTypeType, final String... fields) {
		final DataTypeId someId = EXAMPLE_IDS.get(dataTypeType);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(dataTypeType, packFieldPath(asList(fields)));
		executeQueryAndLookupResponseAtPath(queryBuilder.build(), queryPath(dataTypeType, fields));
	}

	private <T> void assertFieldInSomeEntityOfTypeEqualsValue(final String dataTypeType, T value, final String... fields) {
		final DataTypeId someId = EXAMPLE_IDS.get(dataTypeType);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(dataTypeType, packFieldPath(asList(fields)));
		assertEquals(value, executeQueryAndLookupResponseAtPath(queryBuilder.build(), queryPath(dataTypeType, fields)));
	}

	private String packFieldPath(List<String> fields) {
		reverse(fields);
		String queryString = null;
		for (final String field : fields) {
			if (queryString != null) {
				queryString = "{" + queryString + "}";
				queryString = field + queryString;
			} else {
				queryString = field;
			}
		}
		reverse(fields);
		return queryString;
	}

	private String[] queryPath(String dataTypeType, String... fields) {
		final String[] path = new String[fields.length + 2];
		path[0] = "data";
		path[1] = dataTypeType;
		for (int i = 0; i < fields.length; i++) {
			path[i + 2] = fields[i];
		}
		return path;
	}

	@SuppressWarnings("unchecked")
	private <T> T executeQueryAndLookupResponseAtPath(final ParameterGroup query, String... path) {
		final Map<String, Object> response = executeQuery(query);
		return (T) lookupFirstValidReponseElement(response, path);
	}

	private Map<String, Object> executeQuery(final ParameterGroup parameterGroup) {
		return httpClient.loadUrlAsObject(NEXUS_SERVICE, GRAPHQL, GET, JSON, parameterGroup);
	}

	@SuppressWarnings("unchecked")
	private Object lookupFirstValidReponseElement(Map<String, Object> response, String... path) {
		try {
			Object result = response;
			for (int i = 0; i < path.length; i++) {
				while (result instanceof List) {
					result = ((List<?>) result).get(0);
				}
				result = ((Map<String, Object>) result).get(path[i]);
			}
			return result;
		} catch (final Exception e) {
			throw new RuntimeException("Unable to lookup path " + stream(path).collect(joining(".")) + " in response: " + response, e);
		}
	}

	private DataTypeId firstId(List<DocumentInfo> list) {
		return list.get(0)
			.getId();
	}

	private void assertIsEmpty(List<?> list) {
		assertTrue(list.isEmpty());
	}

	private void assertContainsExpectedText(String expectedText, final String response) {
		assertTrue("Unable to find text '" + expectedText + "' in " + response, response.contains(expectedText));
	}
}