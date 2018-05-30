package net.thomas.portfolio.service_commons.services;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.Credentials;
import net.thomas.portfolio.common.services.Parameter;
import net.thomas.portfolio.common.services.ParameterGroup;
import net.thomas.portfolio.common.services.ServiceDependency;
import net.thomas.portfolio.services.Service;
import net.thomas.portfolio.services.ServiceEndpoint;

public class HttpRestClient {
	private static final int MAX_INSTANCE_LOOKUP_ATTEMPTS = 60;
	private final EurekaClient discoveryClient;
	private final RestTemplate restTemplate;
	private final ServiceDependency serviceInfo;

	public HttpRestClient(EurekaClient discoveryClient, RestTemplate restTemplate, ServiceDependency serviceInfo) {
		this.discoveryClient = discoveryClient;
		this.restTemplate = restTemplate;
		this.serviceInfo = serviceInfo;
	}

	public <T> T loadUrlAsObject(Service service, ServiceEndpoint endpoint, Class<T> responseType) {
		final URI request = buildUri(service, endpoint);
		return execute(request, responseType);
	}

	public <T> T loadUrlAsObject(Service service, ServiceEndpoint endpoint, Class<T> responseType, ParameterGroup... parameters) {
		final URI request = buildUri(service, endpoint, parameters);
		return execute(request, responseType);
	}

	public <T> T loadUrlAsObject(Service service, ServiceEndpoint endpoint, Class<T> responseType, Parameter... parameters) {
		final URI request = buildUri(service, endpoint, parameters);
		return execute(request, responseType);
	}

	private <T> T execute(final URI request, Class<T> responseType) {
		try {
			final long stamp = System.nanoTime();
			final ResponseEntity<T> response = restTemplate.exchange(request, GET, buildRequestHeader(serviceInfo.getCredentials()), responseType);
			System.out.println("Spend " + (System.nanoTime() - stamp) / 1000000.0 + " ms calling " + request);
			if (OK.equals(response.getStatusCode())) {
				return response.getBody();
			} else {
				throw new RuntimeException("Unable to execute request for '" + request + "'. Please verify " + serviceInfo.getName() + " is working properly.");
			}
		} catch (final HttpClientErrorException e) {
			if (NOT_FOUND.equals(e.getStatusCode())) {
				return null;
			} else {
				throw new RuntimeException("Unable to execute request for '" + request + "'. Please verify " + serviceInfo.getName() + " is working properly.",
						e);
			}
		}
	}

	public <T> T loadUrlAsObject(Service service, ServiceEndpoint endpoint, ParameterizedTypeReference<T> responseType, ParameterGroup... parameters) {
		final URI request = buildUri(service, endpoint, parameters);
		return execute(request, responseType);
	}

	public <T> T loadUrlAsObject(Service service, ServiceEndpoint endpoint, ParameterizedTypeReference<T> responseType, Parameter... parameters) {
		final URI request = buildUri(service, endpoint, parameters);
		return execute(request, responseType);
	}

	private <T> T execute(final URI request, ParameterizedTypeReference<T> responseType) {
		try {
			final ResponseEntity<T> response = restTemplate.exchange(request, GET, buildRequestHeader(serviceInfo.getCredentials()), responseType);
			if (OK.equals(response.getStatusCode())) {
				return response.getBody();
			} else {
				throw new RuntimeException("Unable to execute request for '" + request + "'. Please verify " + serviceInfo.getName() + " is working properly.");
			}
		} catch (final HttpClientErrorException e) {
			if (NOT_FOUND.equals(e.getStatusCode())) {
				return null;
			} else {
				throw new RuntimeException("Unable to execute request for '" + request + "'. Please verify " + serviceInfo.getName() + " is working properly.",
						e);
			}
		}
	}

	private HttpEntity<String> buildRequestHeader(final Credentials credentials) {
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + credentials.getEncoded());
		return new HttpEntity<>(headers);
	}

	private InstanceInfo getServiceInfo(String serviceName) {
		InstanceInfo instanceInfo = null;
		int tries = 0;
		while (instanceInfo == null && tries < MAX_INSTANCE_LOOKUP_ATTEMPTS) {
			try {
				instanceInfo = discoveryClient.getNextServerFromEureka(serviceName, false);
			} catch (final RuntimeException e) {
				if (e.getMessage()
						.contains("No matches for the virtual host")) {
					tries++;
					System.out
							.println("Failed discovery of " + serviceInfo.getName() + ". Retrying " + (MAX_INSTANCE_LOOKUP_ATTEMPTS - tries) + " more times.");
					try {
						Thread.sleep(5000);
					} catch (final InterruptedException e1) {
					}
				} else {
					throw new RuntimeException("Unable to perform GET", e);
				}
			}
		}
		if (instanceInfo == null && tries == MAX_INSTANCE_LOOKUP_ATTEMPTS) {
			throw new RuntimeException("Unable to locate " + serviceInfo.getName() + " in discovery service");
		} else if (tries > 0) {
			System.out.println("Discovery of " + serviceInfo.getName() + " successful.");
		}
		return instanceInfo;
	}

	private URI buildUri(Service serviceId, ServiceEndpoint endpoint) {
		return buildUri(serviceId, endpoint, emptySet());
	}

	private URI buildUri(Service serviceId, ServiceEndpoint endpoint, Parameter... parameters) {
		return buildUri(serviceId, endpoint, asList(parameters));
	}

	private URI buildUri(Service service, ServiceEndpoint endpoint, ParameterGroup... groups) {
		final Collection<Parameter> parameters = stream(groups)
				.map(ParameterGroup::getParameters)
				.flatMap(Arrays::stream)
				.collect(Collectors.toList());
		return buildUri(service, endpoint, parameters);
	}

	private URI buildUri(Service serviceId, ServiceEndpoint endpoint, Collection<Parameter> parameters) {
		final InstanceInfo instanceInfo = getServiceInfo(serviceInfo.getName());
		final String serviceUrl = instanceInfo.getHomePageUrl();
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUrl + buildResourceUrl(serviceId, endpoint));
		addParametersToBuilder(builder, parameters);
		return builder.build()
				.encode()
				.toUri();
	}

	private void addParametersToBuilder(UriComponentsBuilder builder, Collection<Parameter> parameters) {
		for (final Parameter parameter : parameters) {
			if (parameter.getValue() != null) {
				builder.queryParam(parameter.getName(), parameter.getValue());
			}
		}
	}

	private String buildResourceUrl(Service serviceId, ServiceEndpoint endpoint) {
		return serviceId.getPath() + endpoint.getPath();
	}
}