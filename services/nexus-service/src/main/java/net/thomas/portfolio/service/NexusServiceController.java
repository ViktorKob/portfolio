package net.thomas.portfolio.service;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import graphql.servlet.SimpleGraphQLServlet;
import net.thomas.portfolio.graphql.GraphQlModelBuilder;
import net.thomas.portfolio.service_commons.services.HbaseModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.services.RenderingAdaptorImpl;

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
		final GraphQlModelBuilder builder = new GraphQlModelBuilder();
		builder.setHbaseModelAdaptor(new HbaseModelAdaptorImpl(hbaseIndexClient));
		builder.setRenderingAdaptor(new RenderingAdaptorImpl(renderingClient));
		return new ServletRegistrationBean(new SimpleGraphQLServlet(builder.build()), "/schema.json", GRAPHQL_SERVLET_MAPPING);
	}
}
