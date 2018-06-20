package net.thomas.portfolio.render.service;

import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_HTML_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_SIMPLE_REPRESENTATION_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_TEXT_PATH;
import static org.springframework.http.ResponseEntity.badRequest;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.render.common.context.HtmlRenderContextBuilder;
import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContextBuilder;
import net.thomas.portfolio.render.common.context.TextRenderContextBuilder;
import net.thomas.portfolio.render.format.html.HtmlRenderControl;
import net.thomas.portfolio.render.format.simple_rep.HbaseIndexingModelSimpleRepresentationRendererLibrary;
import net.thomas.portfolio.render.format.text.HbaseIndexingModelTextRendererLibrary;
import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@Controller
public class RenderServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);

	private final RenderServiceConfiguration config;
	private final HbaseIndexingModelSimpleRepresentationRendererLibrary simpleRepRenderer;
	private final HbaseIndexingModelTextRendererLibrary textRenderer;
	private final HtmlRenderControl htmlRenderer;
	@Autowired
	private EurekaClient discoveryClient;
	private HbaseIndexModelAdaptorImpl hbaseIndexAdaptor;

	public RenderServiceController(RenderServiceConfiguration config) {
		this.config = config;
		simpleRepRenderer = new HbaseIndexingModelSimpleRepresentationRendererLibrary();
		textRenderer = new HbaseIndexingModelTextRendererLibrary();
		htmlRenderer = new HtmlRenderControl();
	}

	@PostConstruct
	public void prepareForRendering() {
		new Thread(() -> {
			hbaseIndexAdaptor = new HbaseIndexModelAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getHbaseIndexing()));
			TYPE.setValidStrings(hbaseIndexAdaptor.getDataTypes());
		}).run();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Secured("ROLE_USER")
	@RequestMapping(RENDER_AS_SIMPLE_REPRESENTATION_PATH)
	public ResponseEntity<String> renderAsSimpleRepresentation(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = hbaseIndexAdaptor.getDataType(id);
			if (entity != null) {
				return ResponseEntity.ok(simpleRepRenderer.render(entity, new SimpleRepresentationRenderContextBuilder().setHbaseModelAdaptor(hbaseIndexAdaptor)
					.build()));
			} else {
				return ResponseEntity.notFound()
					.build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(RENDER_AS_TEXT_PATH)
	public ResponseEntity<String> renderAsText(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = hbaseIndexAdaptor.getDataType(id);
			if (entity != null) {
				return ResponseEntity.ok(textRenderer.render(entity, new TextRenderContextBuilder().build()));
			} else {
				return ResponseEntity.notFound()
					.build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(RENDER_AS_HTML_PATH)
	public ResponseEntity<String> renderAsHtml(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = hbaseIndexAdaptor.getDataType(id);
			if (entity != null) {
				return ResponseEntity.ok(htmlRenderer.render(entity, new HtmlRenderContextBuilder().build()));
			} else {
				return ResponseEntity.notFound()
					.build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}
}