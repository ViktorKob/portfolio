package net.thomas.portfolio.nexus.service;

import static java.util.Collections.sort;
import static net.thomas.portfolio.service_commons.network.urls.UrlFactory.usingPortfolio;
import static org.springframework.http.ResponseEntity.ok;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import io.swagger.annotations.Api;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlSuffixBuilder;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;

@RestController
@Api(value = "", description = "HATEOAS index for the portfolio infrastructure")
@EnableConfigurationProperties
@RequestMapping(produces = "application/hal+json")
public class PortfolioIndexController {
	private final NexusServiceConfiguration config;
	@Value("${global-url-prefix}")
	private String globalUrlPrefix;
	@Autowired
	private EurekaClient discoveryClient;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private HbaseIndexModelAdaptor hbaseAdaptor;

	private PortfolioHateoasWrappingHelper hateoasHelper;
	private List<String> documentTypes;
	private List<String> selectorTypes;

	public PortfolioIndexController(NexusServiceConfiguration config) {
		this.config = config;
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
			documentTypes = new LinkedList<>(hbaseAdaptor.getDocumentTypes());
			sort(documentTypes);
			selectorTypes = new LinkedList<>(hbaseAdaptor.getSelectorTypes());
			sort(selectorTypes);
		}).start();
	}

	@RequestMapping(path = { "/", "/portfolio", "/index" }, produces = "application/hal+json")
	public ResponseEntity<?> getPortfolioRoot() {
		return ok(hateoasHelper.wrapWithRootLinks("HATEOAS index", documentTypes, selectorTypes));
	}
}