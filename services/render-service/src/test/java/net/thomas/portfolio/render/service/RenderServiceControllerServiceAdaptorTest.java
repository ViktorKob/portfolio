package net.thomas.portfolio.render.service;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
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

import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.RenderingAdaptorImpl;
import net.thomas.portfolio.service_testing.TestCommunicationWiringTool;
import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = { "server.name=render-service", "server.port:18150", "eureka.client.registerWithEureka:false",
		"eureka.client.fetchRegistry:false" })
public class RenderServiceControllerServiceAdaptorTest {
	private static final TestCommunicationWiringTool COMMUNICATION_WIRING = new TestCommunicationWiringTool("render-service",
			18150);

	private static final String DATA_TYPE = "TYPE";
	private static final String UID = "FF";
	private static final DataTypeId DATA_TYPE_ID = new DataTypeId(DATA_TYPE, UID);
	private static final DataType ENTITY = new DataType();
	private static final String RENDERED_ENTITY = "RENDERED";

	@TestConfiguration
	static class ServiceMocksSetup {
		@Bean
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
	private static RenderingAdaptorImpl renderingAdaptor;

	@Before
	public void setUpController() {
		reset(rendererProvider);
		COMMUNICATION_WIRING.setRestTemplate(restTemplate);
		renderingAdaptor = new RenderingAdaptorImpl();
		renderingAdaptor.initialize(COMMUNICATION_WIRING.setupMockAndGetHttpClient());
	}

	@Test
	public void shouldLookupDataTypeAndRenderAsSimpleRep() {
		when(rendererProvider.renderAsSimpleRep(eq(ENTITY), any())).thenReturn(RENDERED_ENTITY);
		final String simpleRep = renderingAdaptor.renderAsSimpleRepresentation(DATA_TYPE_ID);
		assertEquals(RENDERED_ENTITY, simpleRep);
	}

	@Test
	public void shouldLookupDataTypeAndRenderAsText() {
		when(rendererProvider.renderAsText(eq(ENTITY), any())).thenReturn(RENDERED_ENTITY);
		final String simpleRep = renderingAdaptor.renderAsText(DATA_TYPE_ID);
		assertEquals(RENDERED_ENTITY, simpleRep);
	}

	@Test
	public void shouldLookupDataTypeAndRenderAsHtml() {
		when(rendererProvider.renderAsHtml(eq(ENTITY), any())).thenReturn(RENDERED_ENTITY);
		final String simpleRep = renderingAdaptor.renderAsHtml(DATA_TYPE_ID);
		assertEquals(RENDERED_ENTITY, simpleRep);
	}
}