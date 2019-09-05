package net.thomas.portfolio.nexus.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLObjectMapper;
import net.thomas.portfolio.common.services.parameters.ServiceDependency;
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
import net.thomas.portfolio.service_commons.network.PortfolioUrlSuffixBuilder;
import net.thomas.portfolio.service_commons.network.ServiceDiscoveryUrlPrefixBuilder;
import net.thomas.portfolio.service_commons.network.UrlFactory;

@SpringBootApplication
public class NexusServiceController {
	private final NexusServiceConfiguration config;
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
		adaptors = buildCompositeAdaptors();
	}

	private void initializeIndividualAdaptors() {
		buildUrlFactoryFor(config.getAnalytics());
		((PortfolioInfrastructureAware) analyticsAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getAnalytics()));
		buildUrlFactoryFor(config.getHbaseIndexing());
		((PortfolioInfrastructureAware) hbaseAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getHbaseIndexing()));
		buildUrlFactoryFor(config.getLegal());
		((PortfolioInfrastructureAware) legalAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getLegal()));
		buildUrlFactoryFor(config.getRendering());
		((PortfolioInfrastructureAware) renderingAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getRendering()));
		buildUrlFactoryFor(config.getUsage());
		((PortfolioInfrastructureAware) usageAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getUsage()));
	}

	private UrlFactory buildUrlFactoryFor(ServiceDependency serviceInfo) {
		return new UrlFactory(new ServiceDiscoveryUrlPrefixBuilder(discoveryClient, serviceInfo), new PortfolioUrlSuffixBuilder());
	}

	private Adaptors buildCompositeAdaptors() {
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