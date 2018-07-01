package net.thomas.portfolio.render.service;

import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_HTML_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_SIMPLE_REPRESENTATION_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_TEXT_PATH;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

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
import net.thomas.portfolio.render.common.context.HtmlRenderContext;
import net.thomas.portfolio.render.common.context.HtmlRenderContextBuilder;
import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContext;
import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContextBuilder;
import net.thomas.portfolio.render.common.context.TextRenderContext;
import net.thomas.portfolio.render.common.context.TextRenderContextBuilder;
import net.thomas.portfolio.render.format.html.HtmlRenderControl;
import net.thomas.portfolio.render.format.simple_rep.HbaseIndexingModelSimpleRepresentationRendererLibrary;
import net.thomas.portfolio.render.format.text.HbaseIndexingModelTextRendererLibrary;
import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;
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
	@Autowired
	private HbaseIndexModelAdaptor hbaseAdaptor;
	@Autowired
	private RestTemplate restTemplate;

	public RenderServiceController(RenderServiceConfiguration config) {
		this.config = config;
		simpleRepRenderer = new HbaseIndexingModelSimpleRepresentationRendererLibrary();
		textRenderer = new HbaseIndexingModelTextRendererLibrary();
		htmlRenderer = new HtmlRenderControl();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public HbaseIndexModelAdaptor getHbaseIndexModelAdaptor() {
		return new HbaseIndexModelAdaptorImpl();
	}

	@PostConstruct
	public void initializeService() {
		((HbaseIndexModelAdaptorImpl) hbaseAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getHbaseIndexing()));
		new Thread(() -> {
			TYPE.setValidStrings(hbaseAdaptor.getDataTypes());
		}).run();
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = RENDER_AS_SIMPLE_REPRESENTATION_PATH + "/{dti_type}/{dti_uid}", method = GET)
	public ResponseEntity<String> renderAsSimpleRepresentation(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = hbaseAdaptor.getDataType(id);
			if (entity != null) {
				final SimpleRepresentationRenderContext renderContext = new SimpleRepresentationRenderContextBuilder().setHbaseModelAdaptor(hbaseAdaptor)
					.build();
				return ok(simpleRepRenderer.render(entity, renderContext));
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = RENDER_AS_TEXT_PATH + "/{dti_type}/{dti_uid}", method = GET)
	public ResponseEntity<String> renderAsText(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = hbaseAdaptor.getDataType(id);
			if (entity != null) {
				final TextRenderContext renderContext = new TextRenderContextBuilder().build();
				return ok(textRenderer.render(entity, renderContext));
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = RENDER_AS_HTML_PATH + "/{dti_type}/{dti_uid}", method = GET)
	public ResponseEntity<String> renderAsHtml(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = hbaseAdaptor.getDataType(id);
			if (entity != null) {
				final HtmlRenderContext renderContext = new HtmlRenderContextBuilder().build();
				return ok(htmlRenderer.render(entity, renderContext));
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}
}