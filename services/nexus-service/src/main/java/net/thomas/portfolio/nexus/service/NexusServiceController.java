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
import net.thomas.portfolio.service_commons.services.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.services.LegalAdaptorImpl;
import net.thomas.portfolio.service_commons.services.RenderingAdaptorImpl;
import net.thomas.portfolio.service_commons.services.UsageAdaptorImpl;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;

@SpringBootApplication
public class NexusServiceController {

	private static final String GRAPHQL_SERVLET_MAPPING = NEXUS_SERVICE_PATH + "/*";
	private final NexusServiceConfiguration config;
	@Autowired
	private EurekaClient discoveryClient;
	private Adaptors adaptors;

	public NexusServiceController(NexusServiceConfiguration config) {
		this.config = config;
	}

	@PostConstruct
	public void buildHttpClient() {
		final AnalyticsAdaptorImpl analyticsAdaptor = new AnalyticsAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getAnalytics()));
		final HbaseIndexModelAdaptorImpl hbaseIndexAdaptor = new HbaseIndexModelAdaptorImpl(
				new HttpRestClient(discoveryClient, getRestTemplate(), config.getHbaseIndexing()));
		final LegalAdaptorImpl legalAdaptor = new LegalAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getLegal()));
		final RenderingAdaptorImpl renderingAdaptor = new RenderingAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getRendering()));
		final UsageAdaptorImpl usageAdaptor = new UsageAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getUsage()));
		adaptors = new Adaptors(analyticsAdaptor, hbaseIndexAdaptor, legalAdaptor, renderingAdaptor, usageAdaptor);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean
	public ServletRegistrationBean graphQLServletRegistrationBean() throws IOException {
		final GraphQlModelBuilder schemaBuilder = new GraphQlModelBuilder();
		schemaBuilder.setAdaptor(adaptors);
		final Builder servletBuilder = SimpleGraphQLServlet.builder(schemaBuilder.build());
		return new ServletRegistrationBean(servletBuilder.build(), "/schema.json", GRAPHQL_SERVLET_MAPPING, "/graphql/*");
	}
}