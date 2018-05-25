package net.thomas.portfolio.service;

import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.RecognitionLevel.UNKNOWN;

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
import net.thomas.portfolio.graphql.GraphQlModelBuilder;
import net.thomas.portfolio.service_commons.services.HbaseModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.services.RenderingAdaptorImpl;
import net.thomas.portfolio.shared_objects.adaptors.AnalyticsAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

@SpringBootApplication
public class NexusServiceController {

	private static final String GRAPHQL_SERVLET_MAPPING = "/graphql/*";
	private final NexusServiceConfiguration configuration;
	@Autowired
	private EurekaClient discoveryClient;
	private HttpRestClient hbaseIndexClient;
	private HttpRestClient renderingClient;

	public NexusServiceController(NexusServiceConfiguration configuration) {
		this.configuration = configuration;
	}

	@PostConstruct
	public void buildHttpClient() {
		hbaseIndexClient = new HttpRestClient(discoveryClient, getRestTemplate(), configuration.getHbaseIndexing());
		renderingClient = new HttpRestClient(discoveryClient, getRestTemplate(), configuration.getRendering());
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean
	public ServletRegistrationBean graphQLServletRegistrationBean() throws IOException {
		final GraphQlModelBuilder schemaBuilder = new GraphQlModelBuilder();
		schemaBuilder.setHbaseModelAdaptor(new HbaseModelAdaptorImpl(hbaseIndexClient));
		schemaBuilder.setRenderingAdaptor(new RenderingAdaptorImpl(renderingClient));
		schemaBuilder.setAnalyticsAdaptor(new AnalyticsAdaptor() {
			@Override
			public PreviousKnowledge getPreviousKnowledgeFor(Selector selector) {
				return new PreviousKnowledge(UNKNOWN, UNKNOWN);
			}
		});
		final Builder servletBuilder = SimpleGraphQLServlet.builder(schemaBuilder.build());
		return new ServletRegistrationBean(servletBuilder.build(), "/schema.json", GRAPHQL_SERVLET_MAPPING);
	}
}