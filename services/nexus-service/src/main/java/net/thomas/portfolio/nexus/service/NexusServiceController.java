package net.thomas.portfolio.nexus.service;

import static graphql.servlet.SimpleGraphQLServlet.builder;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import graphql.servlet.GraphQLSchemaProvider;
import graphql.servlet.SimpleGraphQLServlet;
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
import net.thomas.portfolio.service_commons.network.HttpRestClientInitializable;

@SpringBootApplication
public class NexusServiceController {
	private final NexusServiceConfiguration config;
	@Autowired
	private EurekaClient discoveryClient;
	@Autowired
	private AnalyticsAdaptor analyticsAdaptor;
	@Autowired
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

	@Bean(name = "HbaseIndexModelAdaptor")
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
		((HttpRestClientInitializable) analyticsAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getAnalytics()));
		((HttpRestClientInitializable) hbaseAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getHbaseIndexing()));
		((HttpRestClientInitializable) legalAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getLegal()));
		((HttpRestClientInitializable) renderingAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getRendering()));
		((HttpRestClientInitializable) usageAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getUsage()));
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
	public ServletRegistrationBean<SimpleGraphQLServlet> graphQLServletRegistrationBean() throws IOException {
		final GraphQLSchemaProvider schemaProvider = new GraphQlModelBuilder().setAdaptors(adaptors).build();
		final SimpleGraphQLServlet servlet = builder(schemaProvider).withGraphQLErrorHandler(new CustomErrorHandler()).build();
		return new ServletRegistrationBean<>(servlet, "/schema.json", "/graphql/*");
	}
}