package net.thomas.portfolio.render.service;

import static java.util.Arrays.asList;
import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceParameters.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.RenderServiceProperties.loadRenderConfigurationIntoProperties;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
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
import net.thomas.portfolio.service_commons.adaptors.impl.RenderingAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_testing.TestCommunicationWiringTool;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = { "server.port:18150", "eureka.client.registerWithEureka:false",
		"eureka.client.fetchRegistry:false" })
public class RenderServiceControllerServiceAdaptorTest {
	private static final TestCommunicationWiringTool COMMUNICATION_WIRING = new TestCommunicationWiringTool("render-service", 18150);

	@BeforeClass
	public static void setupContextPath() {
		loadRenderConfigurationIntoProperties();
		loadDefaultServiceConfigurationIntoProperties();
		loadServicePathsIntoProperties();
	}

	@TestConfiguration
	static class ServiceMocksSetup {
		@Bean(name = "HbaseIndexModelAdaptor")
		public HbaseIndexModelAdaptor getHbaseAdaptor() {
			final HbaseIndexModelAdaptorImpl adaptor = mock(HbaseIndexModelAdaptorImpl.class);
			when(adaptor.getDataTypes()).thenReturn(asList(DATA_TYPE));
			when(adaptor.getDataType(eq(DATA_TYPE_ID))).thenReturn(ENTITY);
			return adaptor;
		}

		@Bean
		public RendererProvider getRendererProvider() {
			return mock(RendererProvider.class);
		}
	}

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RendererProvider rendererProvider;
	private Adaptors adaptors;

	@Before
	public void setUpController() {
		reset(rendererProvider);
		COMMUNICATION_WIRING.setRestTemplate(restTemplate);
		final RenderingAdaptorImpl renderingAdaptor = new RenderingAdaptorImpl();
		renderingAdaptor.initialize(COMMUNICATION_WIRING.setupMockAndGetHttpClient());
		adaptors = new Adaptors.Builder().setRenderingAdaptor(renderingAdaptor)
			.build();
	}

	@Test
	public void shouldLookupDataTypeAndRenderAsSimpleRep() {
		when(rendererProvider.renderAsSimpleRep(eq(ENTITY), any())).thenReturn(RENDERED_ENTITY);
		final String simpleRep = adaptors.renderAsSimpleRepresentation(DATA_TYPE_ID);
		assertEquals(RENDERED_ENTITY, simpleRep);
	}

	@Test
	public void shouldLookupDataTypeAndRenderAsText() {
		when(rendererProvider.renderAsText(eq(ENTITY), any())).thenReturn(RENDERED_ENTITY);
		final String simpleRep = adaptors.renderAsText(DATA_TYPE_ID);
		assertEquals(RENDERED_ENTITY, simpleRep);
	}

	@Test
	public void shouldLookupDataTypeAndRenderAsHtml() {
		when(rendererProvider.renderAsHtml(eq(ENTITY), any())).thenReturn(RENDERED_ENTITY);
		final String simpleRep = adaptors.renderAsHtml(DATA_TYPE_ID);
		assertEquals(RENDERED_ENTITY, simpleRep);
	}

	private static final String DATA_TYPE = "TYPE";
	private static final DataTypeId DATA_TYPE_ID = new DataTypeId(DATA_TYPE, "FF");
	private static final DataType ENTITY = new DataType();
	private static final String RENDERED_ENTITY = "Rendered";
}