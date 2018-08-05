package net.thomas.portfolio.nexus.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static net.thomas.portfolio.services.Service.NEXUS_SERVICE;
import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceParameters.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.NexusServiceProperties.loadNexusConfigurationIntoProperties;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpMethod.GET;

import java.util.Collection;
import java.util.LinkedHashMap;
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

import net.thomas.portfolio.common.services.parameters.PreSerializedParameter;
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
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

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
			final HbaseIndexModelAdaptor adaptor = buildHbaseAdaptorMock();
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

	@Before
	public void setupController() {
		COMMUNICATION_WIRING.setRestTemplate(restTemplate);
		httpClient = COMMUNICATION_WIRING.setupMockAndGetHttpClient();
	}

	@Test
	public void should() {
		final Map<String, Object> response = httpClient.loadUrlAsObject(NEXUS_SERVICE, GRAPHQL, GET, JSON,
				new PreSerializedParameter("query", "query test($simpleRepresentation:String){Localname(simpleRep:$simpleRepresentation) {uid}}"),
				new PreSerializedParameter("operationName", "test"), jsonParameter("variables", singletonMap("simpleRepresentation", SOME_SIMPLE_REP)));
		assertEquals(SOME_UID, lookupReponseElement(response, "data", "Localname", "uid"));
	}

	@SuppressWarnings("unchecked")
	private Object lookupReponseElement(Map<String, Object> response, String... path) {
		Object result = response;
		for (int i = 0; i < path.length; i++) {
			result = ((Map<String, Object>) result).get(path[i]);
		}
		return result;
	}

	private PreSerializedParameter jsonParameter(String variable, Map<String, String> value) {
		try {
			return new PreSerializedParameter(variable, new ObjectMapper().writeValueAsString(value));
		} catch (final JsonProcessingException e) {
			throw new RuntimeException("Parameter creation failed", e);
		}
	}

	// @Test
	// public void should() {
	// final LinkedHashMap<String, Object> loadUrlAsObject = httpClient.loadUrlAsObject(NEXUS_SERVICE, GRAPHQL, GET, JSON, new PreSerializedParameter("query",
	// "query ExampleSelectorLookup($simpleRepresentation: String, $username: String!, $justification: String) { Localname(simpleRep: $simpleRepresentation) {
	// headline statistics(user: $username, justification: $justification) { dayTotal weekTotal quarterTotal infinityTotal } knowledge { alias isKnown
	// isRestricted } events(user: $username, justification: $justification) { timeOfEvent { timestamp originalTimeZone } ... on Email { from { headline
	// displayedName { name } address { headline localname { name } domain { domainPart domain { domainPart domain { domainPart domain { domainPart domain {
	// domainPart } } } } } } } } } } } "),
	// new PreSerializedParameter("operationName", "ExampleSelectorLookup"), new PreSerializedParameter("variables",
	// "{ \"simpleRepresentation\": \"wxmeipdvhsg\", \"username\": \"me\", \"justification\": \"Because... Reasons...\" }"));
	// System.out.println(loadUrlAsObject);
	// }

	private static final String SOME_SIMPLE_REP = "some simple rep";
	private static final String SOME_UID = "AABB0011";
	private static final String SOME_TYPE = "Some type";
	private static final DataTypeId SOME_ID = new DataTypeId(SOME_TYPE, SOME_UID);
	private static final Collection<String> DATA_TYPES = asList("Localname", "DisplayedName", "Domain", "EmailAddress", "EmailEndpoint", "Email");
	private static final Collection<String> DOCUMENT_TYPES = asList("Email");
	private static final Collection<String> SELECTOR_TYPES = asList("Localname", "DisplayedName", "Domain", "EmailAddress");

	private static HbaseIndexModelAdaptor buildHbaseAdaptorMock() {
		final HbaseIndexModelAdaptor adaptor = mock(HbaseIndexModelAdaptorImpl.class);
		when(adaptor.getDataTypes()).thenReturn(DATA_TYPES);
		when(adaptor.getDocumentTypes()).thenReturn(DOCUMENT_TYPES);
		when(adaptor.getSelectorTypes()).thenReturn(SELECTOR_TYPES);
		for (final String type : DOCUMENT_TYPES) {
			when(adaptor.isDocument(type)).thenReturn(true);
		}
		for (final String type : SELECTOR_TYPES) {
			when(adaptor.isSelector(type)).thenReturn(true);
			when(adaptor.isSimpleRepresentable(type)).thenReturn(true);
		}
		when(adaptor.getFieldsForDataType(any())).thenReturn(new Fields());
		when(adaptor.getIdFromSimpleRep(any(), eq(SOME_SIMPLE_REP))).thenReturn(SOME_ID);
		return adaptor;
	}
}