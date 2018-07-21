package net.thomas.portfolio.service_commons.network;

import static java.lang.System.nanoTime;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.parameters.Credentials;
import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.ServiceDependency;
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

	public <T> T loadUrlAsObject(Service service, ServiceEndpoint endpoint, HttpMethod method, Class<T> responseType) {
		final URI request = buildUri(service, endpoint);
		return execute(request, method, responseType);
	}

	public <T> T loadUrlAsObject(Service service, ServiceEndpoint endpoint, HttpMethod method, Class<T> responseType, ParameterGroup... parameters) {
		final URI request = buildUri(service, endpoint, parameters);
		return execute(request, method, responseType);
	}

	public <T> T loadUrlAsObject(Service service, ServiceEndpoint endpoint, HttpMethod method, Class<T> responseType, Parameter... parameters) {
		final URI request = buildUri(service, endpoint, parameters);
		return execute(request, method, responseType);
	}

	private <T> T execute(final URI request, HttpMethod method, Class<T> responseType) {
		try {
			final long stamp = nanoTime();
			final ResponseEntity<T> response = restTemplate.exchange(request, method, buildRequestHeader(serviceInfo.getCredentials()), responseType);
			System.out.println("Spend " + (System.nanoTime() - stamp) / 1000000.0 + " ms calling " + request);
			if (OK == response.getStatusCode()) {
				return response.getBody();
			} else {
				throw new RuntimeException("Unable to execute request for '" + request + "'. Please verify " + serviceInfo.getName() + " is working properly.");
			}
		} catch (final HttpClientErrorException e) {
			if (NOT_FOUND == e.getStatusCode()) {
				return null;
			} else if (UNAUTHORIZED == e.getStatusCode()) {
				throw new RuntimeException("Access denied for request '" + request + "'. Please verify that you have the correct credentials for the service.",
						e);
			} else {
				throw new RuntimeException("Unable to execute request for '" + request + "'. Please verify " + serviceInfo.getName() + " is working properly.",
						e);
			}
		}
	}

	public <T> T loadUrlAsObject(Service service, ServiceEndpoint endpoint, HttpMethod method, ParameterizedTypeReference<T> responseType,
			ParameterGroup... parameters) {
		final URI request = buildUri(service, endpoint, parameters);
		return execute(request, method, responseType);
	}

	public <T> T loadUrlAsObject(Service service, ServiceEndpoint endpoint, HttpMethod method, ParameterizedTypeReference<T> responseType,
			Parameter... parameters) {
		final URI request = buildUri(service, endpoint, parameters);
		return execute(request, method, responseType);
	}

	private <T> T execute(final URI request, HttpMethod method, ParameterizedTypeReference<T> responseType) {
		try {
			final ResponseEntity<T> response = restTemplate.exchange(request, method, buildRequestHeader(serviceInfo.getCredentials()), responseType);
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

	private URI buildUri(Service serviceId, ServiceEndpoint endpoint) {
		return buildUri(serviceId, endpoint, emptySet());
	}

	private URI buildUri(Service serviceId, ServiceEndpoint endpoint, Parameter... parameters) {
		return buildUri(serviceId, endpoint, asList(parameters));
	}

	private URI buildUri(Service service, ServiceEndpoint endpoint, ParameterGroup... groups) {
		final Collection<Parameter> parameters = stream(groups).map(ParameterGroup::getParameters)
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

	private InstanceInfo getServiceInfo(String serviceName) {
		InstanceInfo instanceInfo = null;
		int tries = 0;
		while (instanceInfo == null && tries < MAX_INSTANCE_LOOKUP_ATTEMPTS) {
			try {
				instanceInfo = discoveryClient.getNextServerFromEureka(serviceName, false);
			} catch (final RuntimeException e) {
				if (e.getMessage()
					.contains("No matches for the virtual host")) {
					System.out
						.println("Failed discovery of " + serviceInfo.getName() + ". Retrying " + (MAX_INSTANCE_LOOKUP_ATTEMPTS - tries - 1) + " more times.");
					try {
						Thread.sleep(5000);
					} catch (final InterruptedException e1) {
					}

				} else {
					throw new RuntimeException("Unable to complete service discovery", e);
				}
			}
			tries++;
		}
		if (instanceInfo == null && tries == MAX_INSTANCE_LOOKUP_ATTEMPTS) {
			throw new RuntimeException("Unable to locate " + serviceInfo.getName() + " in discovery service");
		} else if (tries > 1) {
			System.out.println("Discovery of " + serviceInfo.getName() + " successful.");
		}
		return instanceInfo;
	}

	private void addParametersToBuilder(UriComponentsBuilder builder, Collection<Parameter> parameters) {
		for (final Parameter parameter : parameters) {
			if (parameter.getValue() != null) {
				builder.queryParam(parameter.getName(), parameter.getValue());
			}
		}
	}

	private String buildResourceUrl(Service serviceId, ServiceEndpoint endpoint) {
		return serviceId.getContextPath() + endpoint.getContextPath();
	}
}