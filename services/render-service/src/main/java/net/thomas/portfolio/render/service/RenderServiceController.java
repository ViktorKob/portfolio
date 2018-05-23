package net.thomas.portfolio.render.service;

import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_DATATYPE;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_SCHEMA;
import static net.thomas.portfolio.enums.Service.HBASE_INDEXING_SERVICE;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_HTML_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_SIMPLE_REPRESENTATION_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_TEXT_PATH;
import static net.thomas.portfolio.globals.ServiceGlobals.RENDER_SERVICE_PATH;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.PreSerializedParameter;
import net.thomas.portfolio.hbase_index.fake.HbaseIndexSchemaImpl;
import net.thomas.portfolio.render.common.context.HtmlRenderContextBuilder;
import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContextBuilder;
import net.thomas.portfolio.render.common.context.TextRenderContextBuilder;
import net.thomas.portfolio.render.format.html.HtmlRenderControl;
import net.thomas.portfolio.render.format.simple_rep.HbaseIndexingModelSimpleRepresentationRendererLibrary;
import net.thomas.portfolio.render.format.text.HbaseIndexingModelTextRendererLibrary;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

@Controller
@RequestMapping(RENDER_SERVICE_PATH)
public class RenderServiceController {

	private final RenderServiceConfiguration configuration;
	private final HbaseIndexingModelSimpleRepresentationRendererLibrary simpleRepRenderer;
	private final HbaseIndexingModelTextRendererLibrary textRenderer;
	private final HtmlRenderControl htmlRenderer;
	@Autowired
	private EurekaClient discoveryClient;
	private HttpRestClient hbaseIndexClient;
	private HbaseIndexSchemaImpl schema;

	public RenderServiceController(RenderServiceConfiguration configuration) {
		this.configuration = configuration;
		simpleRepRenderer = new HbaseIndexingModelSimpleRepresentationRendererLibrary();
		textRenderer = new HbaseIndexingModelTextRendererLibrary();
		htmlRenderer = new HtmlRenderControl();
	}

	@PostConstruct
	public void prepareForRendering() {
		hbaseIndexClient = new HttpRestClient(discoveryClient, getRestTemplate(), configuration.getHbaseIndexing());
		schema = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_SCHEMA, HbaseIndexSchemaImpl.class);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Secured("ROLE_USER")
	@RequestMapping(RENDER_AS_SIMPLE_REPRESENTATION_PATH)
	public ResponseEntity<String> renderAsSimpleRepresentation(String type, String uid) {
		final DataType datatype = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATATYPE, DataType.class,
				new PreSerializedParameter("type", type), new PreSerializedParameter("uid", uid));
		return ResponseEntity.ok(simpleRepRenderer.render(datatype, new SimpleRepresentationRenderContextBuilder().setSchema(schema)
			.build()));
	}

	@Secured("ROLE_USER")
	@RequestMapping(RENDER_AS_TEXT_PATH)
	public ResponseEntity<String> renderAsText(String type, String uid) {
		final DataType datatype = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATATYPE, DataType.class,
				new PreSerializedParameter("type", type), new PreSerializedParameter("uid", uid));
		return ResponseEntity.ok(textRenderer.render(datatype, new TextRenderContextBuilder().build()));
	}

	@Secured("ROLE_USER")
	@RequestMapping(RENDER_AS_HTML_PATH)
	public ResponseEntity<String> renderAsHtml(String type, String uid) {
		final DataType datatype = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATATYPE, DataType.class,
				new PreSerializedParameter("type", type), new PreSerializedParameter("uid", uid));
		return ResponseEntity.ok(htmlRenderer.render(datatype, new HtmlRenderContextBuilder().build()));
	}
}