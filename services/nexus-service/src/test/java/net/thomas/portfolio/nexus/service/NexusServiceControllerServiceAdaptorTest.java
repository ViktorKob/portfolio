package net.thomas.portfolio.nexus.service;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.CONTAINER_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.DOCUMENT_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.EXAMPLE_IDS;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SIMPLE_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_DOCUMENT_INFOS;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.SOME_SIMPLE_REP;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestModel.setUpHbaseAdaptorMock;
import static net.thomas.portfolio.services.Service.NEXUS_SERVICE;
import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceParameters.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.NexusServiceProperties.loadNexusConfigurationIntoProperties;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;
import static net.thomas.portfolio.shared_objects.legal.Legality.LEGAL;
import static org.junit.Assert.assertEquals;
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
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;

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
		reset(hbaseAdaptor);
		setUpHbaseAdaptorMock(hbaseAdaptor);
		COMMUNICATION_WIRING.setRestTemplate(restTemplate);
		httpClient = COMMUNICATION_WIRING.setupMockAndGetHttpClient();
		queryBuilder = new GraphQlQueryBuilder();
	}

	// ***************************************
	// *** SelectorFetcher
	// ***************************************
	@Test
	public void shouldLookupSelectorUidAndFetchUid() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "uid");
		assertEquals(someId.uid, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "uid"));
	}

	@Test
	public void shouldReturnErrorWhenSelectorNotPresent() {
		queryBuilder.setNothingToFieldValueQuery(SIMPLE_TYPE, "uid");
		assertContainsExpectedText("uid or simple representation must be specified",
				executeQueryAndLookupResponseAtPath(queryBuilder.build(), "errors", "message"));
	}

	@Test
	public void shouldLookupSelectorSimpleRepAndFetchUid() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		queryBuilder.addVariable("simpleRepresentation", SOME_SIMPLE_REP);
		queryBuilder.setSimpleRepToFieldValueQuery(SIMPLE_TYPE, "uid");
		assertEquals(someId.uid, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "uid"));
	}

	// ***************************************
	// *** SelectorSuggestionsFetcher
	// ***************************************
	@Test
	public void shouldReturnSuggestionsBasedOnSimpleRep() {
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
		final DataTypeId someId = EXAMPLE_IDS.get(CONTAINER_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(CONTAINER_TYPE, "uid");
		assertEquals(someId.uid, executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", CONTAINER_TYPE, "uid"));
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
		when(legalAdaptor.checkLegalityOfInvertedIndexQuery(eq(someId), any())).thenReturn(ILLEGAL);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "events{uid}");
		assertContainsExpectedText("must be justified", executeQueryAndLookupResponseAtPath(queryBuilder.build(), "errors", "message"));
	}

	@Test
	public void shouldReturnEmptyListWhenInvertedIndexLookupAuditLoggingFails() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		when(legalAdaptor.checkLegalityOfInvertedIndexQuery(eq(someId), any())).thenReturn(LEGAL);
		when(legalAdaptor.auditLogInvertedIndexLookup(eq(someId), any())).thenReturn(false);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "events{uid}");
		assertIsEmpty(executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "events"));
	}

	@Test
	public void shouldFetchDocumentInfosForSelector() {
		final DataTypeId someId = EXAMPLE_IDS.get(SIMPLE_TYPE);
		when(legalAdaptor.checkLegalityOfInvertedIndexQuery(eq(someId), any())).thenReturn(LEGAL);
		when(legalAdaptor.auditLogInvertedIndexLookup(eq(someId), any())).thenReturn(true);
		when(hbaseAdaptor.lookupSelectorInInvertedIndex(any())).thenReturn(SOME_DOCUMENT_INFOS);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidToFieldValueQuery(SIMPLE_TYPE, "events{uid}");
		final Object result = executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", SIMPLE_TYPE, "events", "uid");
		assertEquals(firstId(SOME_DOCUMENT_INFOS.getInfos()).uid, result);
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