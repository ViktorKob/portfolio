package net.thomas.portfolio.nexus.service;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.joining;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.COMPLEX_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.DOCUMENT_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.EXAMPLE_IDS;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.RAW_DATA_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SIMPLE_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_DECIMAL;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_DOCUMENT_INFOS;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_GEO_LOCATION;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_INTEGER;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_LONG_INTEGER;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_SIMPLE_REP;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_STRING;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_TIMESTAMP;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.setUpHbaseAdaptorMock;
import static net.thomas.portfolio.services.Service.NEXUS_SERVICE;
import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceParameters.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.NexusServiceProperties.loadNexusConfigurationIntoProperties;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;
import static net.thomas.portfolio.shared_objects.legal.Legality.LEGAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
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
		reset(hbaseAdaptor, legalAdaptor);
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
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "uid");
		assertEquals(someId.uid, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "uid"));
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
		final DataTypeId someId = EXAMPLE_IDS.get(RAW_DATA_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(RAW_DATA_TYPE, "uid");
		assertEquals(someId.uid, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", RAW_DATA_TYPE, "uid"));
	}

	// ***************************************
	// *** DocumentFetcher
	// ***************************************
	@Test
	public void shouldLookupDocumentUidAndFetchUid() {
		final DataTypeId someId = EXAMPLE_IDS.get(DOCUMENT_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(DOCUMENT_TYPE, "uid");
		assertEquals(someId.uid, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", DOCUMENT_TYPE, "uid"));
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