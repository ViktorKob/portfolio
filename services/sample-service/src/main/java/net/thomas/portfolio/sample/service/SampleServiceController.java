package net.thomas.portfolio.sample.service;

import static org.springframework.http.HttpStatus.OK;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

@Controller
public class SampleServiceController {

	@Autowired
	private EurekaClient discoveryClient;

	private String serviceUrl() {
		try {
			final InstanceInfo instanceInfo = discoveryClient.getNextServerFromEureka("graphql-service", false);
			return instanceInfo.getHomePageUrl();
		} catch (final RuntimeException e) {
			throw new RuntimeException("Unable to contact services");
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping("/fetchSimpleRep")
	public ResponseEntity<String> fetchSimpleRep(String type, String uid) {
		final RestTemplate template = loadBalancedRestTemplate();
		try {
			final URI graphQlQuery = buildSimpleRepRequestUrl(type, uid);
			final ResponseEntity<String> response = template.<String>getForEntity(graphQlQuery, String.class);
			if (OK.equals(response.getStatusCode())) {
				return new ResponseEntity<>(response.getBody(), OK);
			} else {
				throw new RuntimeException("Invalid request. Please verify your parameters.");
			}
		} catch (RestClientException | MalformedURLException | UnsupportedEncodingException | URISyntaxException e) {
			throw new RuntimeException("The server is currently unable to complete your request.\nPlease try again in a few minuters.");
		}
	}

	private URI buildSimpleRepRequestUrl(String type, String uid) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
		final long stamp = System.nanoTime();
		final String serviceUrl = serviceUrl();
		System.out.println("Time spend doing service discovery: " + (System.nanoTime() - stamp) / 1000000.0 + " ms");
		return new URL(serviceUrl + "/graphql?query=" + URLEncoder.encode("{" + type + "(uid: \"" + uid + "\"){simpleRep}}", "UTF-8")).toURI();
	}

	@LoadBalanced
	@Bean
	public RestTemplate loadBalancedRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
}