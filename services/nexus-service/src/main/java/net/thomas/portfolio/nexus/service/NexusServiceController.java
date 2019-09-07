package net.thomas.portfolio.nexus.service;

import static net.thomas.portfolio.service_commons.network.urls.UrlFactory.usingPortfolio;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLObjectMapper;
import net.thomas.portfolio.nexus.graphql.GraphQlModelBuilder;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
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
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;

@SpringBootApplication
public class NexusServiceController {
	private final NexusServiceConfiguration config;
	@Value("${global-url-prefix}")
	private String globalUrlPrefix;
	@Autowired
	private EurekaClient discoveryClient;
	@Autowired
	private AnalyticsAdaptor analyticsAdaptor;
	@Autowired
	@Qualifier("HbaseAdaptor")
	private HbaseIndexModelAdaptor hbaseAdaptor;
	@Autowired
	private LegalAdaptor legalAdaptor;
	@Autowired
	private RenderingAdaptor renderingAdaptor;
	@Autowired
	private UsageAdaptor usageAdaptor;
	@Autowired
	private RestTemplate restTemplate;
	private Adaptors adaptors;

	public NexusServiceController(final NexusServiceConfiguration config) {
		this.config = config;
	}

	@Bean(name = "AnalyticsAdaptor")
	public AnalyticsAdaptor getAnalyticsAdaptor() {
		return new AnalyticsAdaptorImpl();
	}

	@Bean(name = "HbaseAdaptor")
	public HbaseIndexModelAdaptor getHbaseIndexModelAdaptor() {
		return new HbaseIndexModelAdaptorImpl();
	}

	@Bean(name = "LegalAdaptor")
	public LegalAdaptor getLegalAdaptor() {
		return new LegalAdaptorImpl();
	}

	@Bean(name = "RenderingAdaptor")
	public RenderingAdaptor getRenderingAdaptor() {
		return new RenderingAdaptorImpl();
	}

	@Bean(name = "UsageAdaptor")
	public UsageAdaptor getUsageAdaptor() {
		return new UsageAdaptorImpl();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@PostConstruct
	public void initializeInfrastructure() {
		initializeIndividualAdaptors();
		adaptors = buildAdaptorsComposite();
	}

	private void initializeIndividualAdaptors() {
		((PortfolioInfrastructureAware) analyticsAdaptor).initialize(usingPortfolio(discoveryClient, config.getAnalytics()),
				new HttpRestClient(restTemplate, config.getAnalytics()));
		((PortfolioInfrastructureAware) hbaseAdaptor).initialize(usingPortfolio(discoveryClient, config.getHbaseIndexing()),
				new HttpRestClient(restTemplate, config.getHbaseIndexing()));
		((PortfolioInfrastructureAware) legalAdaptor).initialize(usingPortfolio(discoveryClient, config.getLegal()),
				new HttpRestClient(restTemplate, config.getLegal()));
		((PortfolioInfrastructureAware) renderingAdaptor).initialize(usingPortfolio(discoveryClient, config.getRendering()),
				new HttpRestClient(restTemplate, config.getRendering()));
		((PortfolioInfrastructureAware) usageAdaptor).initialize(usingPortfolio(discoveryClient, config.getUsage()),
				new HttpRestClient(restTemplate, config.getUsage()));
	}

	private Adaptors buildAdaptorsComposite() {
		return new Adaptors.Builder().setAnalyticsAdaptor(analyticsAdaptor)
				.setHbaseModelAdaptor(hbaseAdaptor)
				.setLegalAdaptor(legalAdaptor)
				.setRenderingAdaptor(renderingAdaptor)
				.setUsageAdaptor(usageAdaptor)
				.build();
	}

	@Bean
	public GraphQLSchema buildSchema() {
		return new GraphQlModelBuilder().setAdaptors(adaptors).build();
	}

	@Bean
	public GraphQLObjectMapper graphQLObjectMapper() {
		return GraphQLObjectMapper.newBuilder().withGraphQLErrorHandler(new CustomErrorHandler()).build();
	}
}