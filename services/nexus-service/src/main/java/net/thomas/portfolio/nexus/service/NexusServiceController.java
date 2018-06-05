package net.thomas.portfolio.nexus.service;

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
import net.thomas.portfolio.service_commons.services.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.services.LegalAdaptorImpl;
import net.thomas.portfolio.service_commons.services.RenderingAdaptorImpl;
import net.thomas.portfolio.service_commons.services.UsageAdaptorImpl;
import net.thomas.portfolio.shared_objects.adaptors.AnalyticsAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.LegalAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.RenderingAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.UsageAdaptor;

@SpringBootApplication
public class NexusServiceController {

	private static final String GRAPHQL_SERVLET_MAPPING = "/*";
	private final NexusServiceConfiguration config;
	@Autowired
	private EurekaClient discoveryClient;
	private AnalyticsAdaptor analyticsAdaptor;
	private HbaseIndexModelAdaptor hbaseIndexAdaptor;
	private LegalAdaptor legalAdaptor;
	private RenderingAdaptor renderingAdaptor;
	private UsageAdaptor usageAdaptor;

	public NexusServiceController(NexusServiceConfiguration config) {
		this.config = config;
	}

	@PostConstruct
	public void buildHttpClient() {
		analyticsAdaptor = new AnalyticsAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getAnalytics()));
		hbaseIndexAdaptor = new HbaseIndexModelAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getHbaseIndexing()));
		legalAdaptor = new LegalAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getLegal()));
		renderingAdaptor = new RenderingAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getRendering()));
		usageAdaptor = new UsageAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getUsage()));
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean
	public ServletRegistrationBean graphQLServletRegistrationBean() throws IOException {
		final GraphQlModelBuilder schemaBuilder = new GraphQlModelBuilder();
		schemaBuilder.setAnalyticsAdaptor(analyticsAdaptor);
		schemaBuilder.setHbaseModelAdaptor(hbaseIndexAdaptor);
		schemaBuilder.setLegalAdaptor(legalAdaptor);
		schemaBuilder.setRenderingAdaptor(renderingAdaptor);
		schemaBuilder.setUsageAdaptor(usageAdaptor);
		final Builder servletBuilder = SimpleGraphQLServlet.builder(schemaBuilder.build());
		return new ServletRegistrationBean(servletBuilder.build(), "/schema.json", GRAPHQL_SERVLET_MAPPING, "/graphql/*");
	}
}