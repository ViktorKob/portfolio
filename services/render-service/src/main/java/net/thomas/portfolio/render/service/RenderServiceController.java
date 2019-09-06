package net.thomas.portfolio.render.service;

import static net.thomas.portfolio.globals.RenderServiceGlobals.AS_HTML_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.AS_SIMPLE_REPRESENTATION_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.AS_TEXT_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_ENTITY_ROOT_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_SELECTOR_ROOT_PATH;
import static net.thomas.portfolio.service_commons.network.urls.UrlFactory.usingPortfolio;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.thomas.portfolio.common.services.parameters.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.render.common.context.HtmlRenderContext;
import net.thomas.portfolio.render.common.context.HtmlRenderContextBuilder;
import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContext;
import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContextBuilder;
import net.thomas.portfolio.render.common.context.TextRenderContext;
import net.thomas.portfolio.render.common.context.TextRenderContextBuilder;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlSuffixBuilder;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@RestController
@Api(value = "", description = "Conversion of entities to different string representations")
@EnableConfigurationProperties
@RequestMapping(produces = "application/hal+json")
public class RenderServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);

	private final RenderServiceConfiguration config;
	@Value("${global-url-prefix}")
	private String globalUrlPrefix;
	@Autowired
	private EurekaClient discoveryClient;
	@Autowired
	private HbaseIndexModelAdaptor hbaseAdaptor;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RendererProvider rendererProvider;
	private PortfolioHateoasWrappingHelper hateoasHelper;

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
		final UrlFactory urlFactory = new UrlFactory(() -> {
			return globalUrlPrefix;
		}, new PortfolioUrlSuffixBuilder());
		hateoasHelper = new PortfolioHateoasWrappingHelper(urlFactory);
		new Thread(() -> {
			((PortfolioInfrastructureAware) hbaseAdaptor).initialize(usingPortfolio(discoveryClient, config.getHbaseIndexing()),
					new HttpRestClient(restTemplate, config.getHbaseIndexing()));
			TYPE.setValidStrings(hbaseAdaptor.getDataTypes());
		}).start();
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Render specific entity using its simple string representation", response = String.class)
	@RequestMapping(path = RENDER_SELECTOR_ROOT_PATH + "/{dti_type}/{dti_uid}" + AS_SIMPLE_REPRESENTATION_PATH, method = GET)
	public ResponseEntity<?> renderAsSimpleRepresentation(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = hbaseAdaptor.getDataType(id);
			if (entity != null) {
				final SimpleRepresentationRenderContext renderContext = new SimpleRepresentationRenderContextBuilder().setHbaseModelAdaptor(hbaseAdaptor)
						.build();
				return ok(hateoasHelper.wrapSimpleRepresentation(rendererProvider.renderAsSimpleRep(entity, renderContext), id));
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Render specific entity as a string", response = String.class)
	@RequestMapping(path = RENDER_ENTITY_ROOT_PATH + "/{dti_type}/{dti_uid}" + AS_TEXT_PATH, method = GET)
	public ResponseEntity<?> renderAsText(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = hbaseAdaptor.getDataType(id);
			if (entity != null) {
				final TextRenderContext renderContext = new TextRenderContextBuilder().build();
				return ok(hateoasHelper.wrapTextualRepresentation(rendererProvider.renderAsText(entity, renderContext), id));
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Render specific entity as HTML with embedded JavaScript", response = String.class)
	@RequestMapping(path = RENDER_ENTITY_ROOT_PATH + "/{dti_type}/{dti_uid}" + AS_HTML_PATH, method = GET)
	public ResponseEntity<?> renderAsHtml(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = hbaseAdaptor.getDataType(id);
			if (entity != null) {
				final HtmlRenderContext renderContext = new HtmlRenderContextBuilder().build();
				return ok(hateoasHelper.wrapHtmlRepresentation(rendererProvider.renderAsHtml(entity, renderContext), id));
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}
}