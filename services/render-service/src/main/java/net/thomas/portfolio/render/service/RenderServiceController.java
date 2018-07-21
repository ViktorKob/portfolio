package net.thomas.portfolio.render.service;

import static net.thomas.portfolio.globals.RenderServiceGlobals.AS_HTML_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.AS_SIMPLE_REPRESENTATION_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.AS_TEXT_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_ENTITY_ROOT_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_SELECTOR_ROOT_PATH;
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

import net.thomas.portfolio.common.services.parameters.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.render.common.context.HtmlRenderContext;
import net.thomas.portfolio.render.common.context.HtmlRenderContextBuilder;
import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContext;
import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContextBuilder;
import net.thomas.portfolio.render.common.context.TextRenderContext;
import net.thomas.portfolio.render.common.context.TextRenderContextBuilder;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.HttpRestClientInitializable;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@Controller
public class RenderServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);

	private final RenderServiceConfiguration config;
	@Autowired
	private EurekaClient discoveryClient;
	@Autowired
	private HbaseIndexModelAdaptor hbaseAdaptor;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RendererProvider rendererProvider;

	public RenderServiceController(RenderServiceConfiguration config) {
		this.config = config;
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean(name = "HbaseIndexModelAdaptor")
	public HbaseIndexModelAdaptor getHbaseAdaptor() {
		return new HbaseIndexModelAdaptorImpl();
	}

	@Bean
	public RendererProvider getRendererProvider() {
		return new RendererProvider();
	}

	@PostConstruct
	public void initializeService() {
		new Thread(() -> {
			((HttpRestClientInitializable) hbaseAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getHbaseIndexing()));
			TYPE.setValidStrings(hbaseAdaptor.getDataTypes());
		}).start();
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = RENDER_SELECTOR_ROOT_PATH + "/{dti_type}/{dti_uid}" + AS_SIMPLE_REPRESENTATION_PATH, method = GET)
	public ResponseEntity<String> renderAsSimpleRepresentation(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = hbaseAdaptor.getDataType(id);
			if (entity != null) {
				final SimpleRepresentationRenderContext renderContext = new SimpleRepresentationRenderContextBuilder().setHbaseModelAdaptor(hbaseAdaptor)
					.build();
				return ok(rendererProvider.renderAsSimpleRep(entity, renderContext));
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = RENDER_ENTITY_ROOT_PATH + "/{dti_type}/{dti_uid}" + AS_TEXT_PATH, method = GET)
	public ResponseEntity<String> renderAsText(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = hbaseAdaptor.getDataType(id);
			if (entity != null) {
				final TextRenderContext renderContext = new TextRenderContextBuilder().build();
				return ok(rendererProvider.renderAsText(entity, renderContext));
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = RENDER_ENTITY_ROOT_PATH + "/{dti_type}/{dti_uid}" + AS_HTML_PATH, method = GET)
	public ResponseEntity<String> renderAsHtml(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = hbaseAdaptor.getDataType(id);
			if (entity != null) {
				final HtmlRenderContext renderContext = new HtmlRenderContextBuilder().build();
				return ok(rendererProvider.renderAsHtml(entity, renderContext));
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}
}