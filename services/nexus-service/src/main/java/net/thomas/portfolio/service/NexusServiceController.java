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
import net.thomas.portfolio.hbase_index.HbaseModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;

@SpringBootApplication
public class NexusServiceController {

	private static final String GRAPHQL_SERVLET_MAPPING = "/graphql/*";
	private final NexusServiceConfiguration configuration;
	@Autowired
	private EurekaClient discoveryClient;
	private HttpRestClient hbaseIndexClient;

	public NexusServiceController(NexusServiceConfiguration configuration) {
		this.configuration = configuration;
	}

	@PostConstruct
	public void buildHttpClient() {
		hbaseIndexClient = new HttpRestClient(discoveryClient, getRestTemplate(), configuration.getHbaseIndexing());
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean
	public ServletRegistrationBean graphQLServletRegistrationBean() throws IOException {
		final GraphQlModelBuilder builder = new GraphQlModelBuilder();
		final HbaseModelAdaptorImpl hbaseModelAdaptorImpl = new HbaseModelAdaptorImpl(hbaseIndexClient);
		builder.setHbaseModelAdaptor(hbaseModelAdaptorImpl);
		return new ServletRegistrationBean(new SimpleGraphQLServlet(builder.build()), "/schema.json", GRAPHQL_SERVLET_MAPPING);
	}
}
