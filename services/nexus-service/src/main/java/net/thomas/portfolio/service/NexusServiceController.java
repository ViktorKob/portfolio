package net.thomas.portfolio.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import graphql.servlet.SimpleGraphQLServlet;
import net.thomas.portfolio.graphql.GraphQlModelBuilder;
import net.thomas.portfolio.hbase_index.GraphQlUtilities;
import net.thomas.portfolio.hbase_index.HbaseModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;

@SpringBootApplication
public class NexusServiceController {

	private static final String GRAPHQL_SERVLET_MAPPING = "/graphql/*";
	private final NexusServiceConfiguration configuration;

	@Autowired
	private EurekaClient discoveryClient;

	public NexusServiceController(NexusServiceConfiguration configuration) {
		this.configuration = configuration;
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean
	public ServletRegistrationBean graphQLServletRegistrationBean() throws IOException {
		final GraphQlModelBuilder builder = new GraphQlModelBuilder(new GraphQlUtilities());
		builder.setName("SampleModel")
			.setDescription("Sample model created to showcase data structure")
			.setHbaseModelAdaptor(new HbaseModelAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), configuration.getHbaseIndexing())));
		return new ServletRegistrationBean(new SimpleGraphQLServlet(builder.build()), "/schema.json", GRAPHQL_SERVLET_MAPPING);
	}
}
