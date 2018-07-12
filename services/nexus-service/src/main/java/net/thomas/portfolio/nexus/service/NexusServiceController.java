package net.thomas.portfolio.nexus.service;

import static net.thomas.portfolio.services.ServiceGlobals.NEXUS_SERVICE_PATH;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import graphql.servlet.SimpleGraphQLServlet;
import graphql.servlet.SimpleGraphQLServlet.Builder;
import net.thomas.portfolio.nexus.graphql.GraphQlModelBuilder;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.services.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.LegalAdaptorImpl;
import net.thomas.portfolio.service_commons.services.RenderingAdaptorImpl;
import net.thomas.portfolio.service_commons.services.UsageAdaptorImpl;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.adaptors.AnalyticsAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.LegalAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.RenderingAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.UsageAdaptor;

@SpringBootApplication
public class NexusServiceController {

	private static final String GRAPHQL_SERVLET_MAPPING = NEXUS_SERVICE_PATH + "/*";
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

	public NexusServiceController(NexusServiceConfiguration config) {
		this.config = config;
	}

	@Bean
	public AnalyticsAdaptor getAnalyticsAdaptor() {
		return new AnalyticsAdaptorImpl();
	}

	@Bean
	public HbaseIndexModelAdaptor getHbaseIndexModelAdaptor() {
		return new HbaseIndexModelAdaptorImpl();
	}

	@Bean
	public LegalAdaptor getLegalAdaptor() {
		return new LegalAdaptorImpl();
	}

	@Bean
	public RenderingAdaptor getRenderingAdaptor() {
		return new RenderingAdaptorImpl();
	}

	@Bean
	public UsageAdaptor getUsageAdaptor() {
		return new UsageAdaptorImpl();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@PostConstruct
	public void buildHttpClient() {
		((AnalyticsAdaptorImpl) analyticsAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getAnalytics()));
		((HbaseIndexModelAdaptorImpl) hbaseAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getHbaseIndexing()));
		((LegalAdaptorImpl) legalAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getLegal()));
		((RenderingAdaptorImpl) renderingAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getRendering()));
		((UsageAdaptorImpl) usageAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getUsage()));
	}

	@Bean
	public ServletRegistrationBean graphQLServletRegistrationBean() throws IOException {
		final GraphQlModelBuilder schemaBuilder = new GraphQlModelBuilder();
		schemaBuilder.setAdaptor(new Adaptors(analyticsAdaptor, hbaseAdaptor, legalAdaptor, renderingAdaptor, usageAdaptor));
		final Builder servletBuilder = SimpleGraphQLServlet.builder(schemaBuilder.build());
		return new ServletRegistrationBean(servletBuilder.build(), "/schema.json", GRAPHQL_SERVLET_MAPPING, "/graphql/*");
	}
}