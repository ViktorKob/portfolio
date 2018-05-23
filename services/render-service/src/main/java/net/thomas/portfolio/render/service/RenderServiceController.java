package net.thomas.portfolio.render.service;

import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_DATA_TYPE;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_SCHEMA;
import static net.thomas.portfolio.enums.Service.HBASE_INDEXING_SERVICE;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_HTML_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_SIMPLE_REPRESENTATION_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_TEXT_PATH;
import static net.thomas.portfolio.globals.ServiceGlobals.RENDER_SERVICE_PATH;
import static org.springframework.http.ResponseEntity.badRequest;

import java.util.Set;

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
import net.thomas.portfolio.common.services.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.common.services.validation.UidValidator;
import net.thomas.portfolio.render.common.context.HtmlRenderContextBuilder;
import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContextBuilder;
import net.thomas.portfolio.render.common.context.TextRenderContextBuilder;
import net.thomas.portfolio.render.format.html.HtmlRenderControl;
import net.thomas.portfolio.render.format.simple_rep.HbaseIndexingModelSimpleRepresentationRendererLibrary;
import net.thomas.portfolio.render.format.text.HbaseIndexingModelTextRendererLibrary;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;

@Controller
@RequestMapping(RENDER_SERVICE_PATH)
public class RenderServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("type", true);
	private static final UidValidator UID = new UidValidator("uid", true);

	private final RenderServiceConfiguration configuration;
	private final HbaseIndexingModelSimpleRepresentationRendererLibrary simpleRepRenderer;
	private final HbaseIndexingModelTextRendererLibrary textRenderer;
	private final HtmlRenderControl htmlRenderer;
	@Autowired
	private EurekaClient discoveryClient;
	private HttpRestClient hbaseIndexClient;
	private HBaseIndexSchemaSerialization schema;

	public RenderServiceController(RenderServiceConfiguration configuration) {
		this.configuration = configuration;
		simpleRepRenderer = new HbaseIndexingModelSimpleRepresentationRendererLibrary();
		textRenderer = new HbaseIndexingModelTextRendererLibrary();
		htmlRenderer = new HtmlRenderControl();
		schema = null;
	}

	@PostConstruct
	public void prepareForRendering() {
		hbaseIndexClient = new HttpRestClient(discoveryClient, getRestTemplate(), configuration.getHbaseIndexing());
		schema = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_SCHEMA, HBaseIndexSchemaSerialization.class);
		final Set<String> dataTypes = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_SCHEMA, HBaseIndexSchemaSerialization.class)
			.getDataTypes();
		TYPE.setValidStrings(dataTypes);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Secured("ROLE_USER")
	@RequestMapping(RENDER_AS_SIMPLE_REPRESENTATION_PATH)
	public ResponseEntity<String> renderAsSimpleRepresentation(String type, String uid) {
		if (TYPE.isValid(type) && UID.isValid(uid)) {
			final DataType datatype = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATA_TYPE, DataType.class,
					new PreSerializedParameter("type", type), new PreSerializedParameter("uid", uid));
			return ResponseEntity.ok(simpleRepRenderer.render(datatype, new SimpleRepresentationRenderContextBuilder().setSchema(schema)
				.build()));
		} else {
			return badRequest().body(TYPE.getReason(type) + "<BR>" + UID.getReason(uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(RENDER_AS_TEXT_PATH)
	public ResponseEntity<String> renderAsText(String type, String uid) {
		if (TYPE.isValid(type) && UID.isValid(uid)) {
			final DataType datatype = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATA_TYPE, DataType.class,
					new PreSerializedParameter("type", type), new PreSerializedParameter("uid", uid));
			return ResponseEntity.ok(textRenderer.render(datatype, new TextRenderContextBuilder().build()));
		} else {
			return badRequest().body(TYPE.getReason(type) + "<BR>" + UID.getReason(uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(RENDER_AS_HTML_PATH)
	public ResponseEntity<String> renderAsHtml(String type, String uid) {
		if (TYPE.isValid(type) && UID.isValid(uid)) {
			final DataType datatype = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATA_TYPE, DataType.class,
					new PreSerializedParameter("type", type), new PreSerializedParameter("uid", uid));
			return ResponseEntity.ok(htmlRenderer.render(datatype, new HtmlRenderContextBuilder().build()));
		} else {
			return badRequest().body(TYPE.getReason(type) + "<BR>" + UID.getReason(uid));
		}
	}
}