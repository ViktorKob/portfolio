package net.thomas.portfolio.nexus.service;

import static net.thomas.portfolio.services.Service.NEXUS_SERVICE;
import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceParameters.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.NexusServiceProperties.loadNexusConfigurationIntoProperties;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpMethod.GET;

import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

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

	@BeforeClass
	public static void setupContextPath() {
		loadNexusConfigurationIntoProperties();
		loadDefaultServiceConfigurationIntoProperties();
		loadServicePathsIntoProperties();
	}

	@MockBean(name = "AnalyticsAdaptor", classes = { AnalyticsAdaptorImpl.class })
	private AnalyticsAdaptor analyticsAdaptor;
	@MockBean(name = "HbaseIndexModelAdaptor", classes = { HbaseIndexModelAdaptorImpl.class })
	private HbaseIndexModelAdaptor hbaseAdaptor;
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
		final LinkedHashMap<String, Object> loadUrlAsObject = httpClient.loadUrlAsObject(NEXUS_SERVICE, GRAPHQL, GET, JSON, new PreSerializedParameter("query",
				"query ExampleSelectorLookup($simpleRepresentation: String, $username: String!, $justification: String) {  Localname(simpleRep: $simpleRepresentation) {   headline   statistics(user: $username, justification: $justification) {    dayTotal    weekTotal    quarterTotal    infinityTotal   }   knowledge {    alias    isKnown    isRestricted   }   events(user: $username, justification: $justification) {    timeOfEvent {     timestamp     originalTimeZone    }    ... on Email {     from {      headline      displayedName {       name      }      address {       headline       localname {        name       }       domain {        domainPart        domain {         domainPart         domain {          domainPart          domain {           domainPart           domain {            domainPart           }          }         }        }       }      }     }    }   }  } } "),
				new PreSerializedParameter("operationName", "ExampleSelectorLookup"), new PreSerializedParameter("variables",
						"{  \"simpleRepresentation\": \"wxmeipdvhsg\",  \"username\": \"me\",  \"justification\": \"Because... Reasons...\" }"));
		System.out.println(loadUrlAsObject);
	}

	private static final DataTypeId SOME_SELECTOR_ID = new DataTypeId("TYPE", "FF");
}