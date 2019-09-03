package net.thomas.portfolio.service_commons.network;

import static java.lang.System.nanoTime;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.parameters.Credentials;
import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.ServiceDependency;
import net.thomas.portfolio.services.Service;
import net.thomas.portfolio.services.ServiceEndpoint;

public class HttpRestClient {
	private static final Logger LOG = getLogger(HttpRestClient.class);
	private static final int MAX_INSTANCE_LOOKUP_ATTEMPTS = 60;
	private final EurekaClient discoveryClient;
	private final RestTemplate restTemplate;
	private final ServiceDependency serviceInfo;
	private final UrlSuffixBuilder urlSuffixBuilder;

	public HttpRestClient(final EurekaClient discoveryClient, final RestTemplate restTemplate, final ServiceDependency serviceInfo) {
		this.discoveryClient = discoveryClient;
		this.restTemplate = restTemplate;
		this.serviceInfo = serviceInfo;
		urlSuffixBuilder = new UrlSuffixBuilder();
	}

	public <T> T loadUrlAsObject(final Service service, final ServiceEndpoint endpoint, final HttpMethod method, final Class<T> responseType) {
		final URI request = buildUri(service, endpoint);
		return execute(request, method, responseType);
	}

	public <T> T loadUrlAsObject(final Service service, final ServiceEndpoint endpoint, final HttpMethod method, final Class<T> responseType,
			final ParameterGroup... parameters) {
		final URI request = buildUri(service, endpoint, parameters);
		return execute(request, method, responseType);
	}

	public <T> T loadUrlAsObject(final Service service, final ServiceEndpoint endpoint, final HttpMethod method, final Class<T> responseType,
			final Parameter... parameters) {
		final URI request = buildUri(service, endpoint, parameters);
		return execute(request, method, responseType);
	}

	@SuppressWarnings("unchecked") // Pending a better solution
	private <T> T execute(final URI request, final HttpMethod method, final Class<T> responseType) {
		final long stamp = nanoTime();
		try {
			final ResponseEntity<T> response = restTemplate.exchange(request, method, buildRequestHeader(serviceInfo.getCredentials()), responseType);
			LOG.info("Spend " + (System.nanoTime() - stamp) / 1000000.0 + " ms executing request '" + request + "'");
			if (OK == response.getStatusCode()) {
				return response.getBody();
			} else if (CREATED == response.getStatusCode()) {
				return (T) (Boolean) true;
			} else {
				throw new RuntimeException("Unable to execute request for '" + request + "'. Please verify " + serviceInfo.getName() + " is working properly.");
			}
		} catch (final HttpClientErrorException e) {
			LOG.error("Spend " + (System.nanoTime() - stamp) / 1000000.0 + " ms failing to execute request '" + request + "'");
			if (NOT_FOUND == e.getStatusCode()) {
				return null;
			} else if (UNAUTHORIZED == e.getStatusCode()) {
				throw new UnauthorizedAccessException(
						"Access denied for request '" + request + "'. Please verify that you have the correct credentials for the service.", e);
			} else if (BAD_REQUEST == e.getStatusCode()) {
				throw new BadRequestException("Request '" + request + "' is malformed. Please fix it before trying again.", e);
			} else {
				throw new RuntimeException("Unable to execute request for '" + request + "'. Please verify " + serviceInfo.getName() + " is working properly.",
						e);
			}
		}
	}

	public <T> T loadUrlAsObject(final Service service, final ServiceEndpoint endpoint, final HttpMethod method,
			final ParameterizedTypeReference<T> responseType, final ParameterGroup... parameters) {
		final URI request = buildUri(service, endpoint, parameters);
		return execute(request, method, responseType);
	}

	public <T> T loadUrlAsObject(final Service service, final ServiceEndpoint endpoint, final HttpMethod method,
			final ParameterizedTypeReference<T> responseType, final Parameter... parameters) {
		final URI request = buildUri(service, endpoint, parameters);
		return execute(request, method, responseType);
	}

	@SuppressWarnings("unchecked") // Pending a better solution
	private <T> T execute(final URI request, final HttpMethod method, final ParameterizedTypeReference<T> responseType) {
		final long stamp = nanoTime();
		try {
			final ResponseEntity<T> response = restTemplate.exchange(request, method, buildRequestHeader(serviceInfo.getCredentials()), responseType);
			LOG.info("Spend " + (System.nanoTime() - stamp) / 1000000.0 + " ms executing request '" + request + "'");
			if (OK.equals(response.getStatusCode())) {
				return response.getBody();
			} else if (CREATED == response.getStatusCode()) {
				return (T) (Boolean) true;
			} else {
				throw new RuntimeException("Unable to execute request for '" + request + "'. Please verify " + serviceInfo.getName() + " is working properly.");
			}
		} catch (final HttpClientErrorException e) {
			LOG.error("Spend " + (System.nanoTime() - stamp) / 1000000.0 + " ms failing to execute request '" + request + "'");
			if (NOT_FOUND.equals(e.getStatusCode())) {
				return null;
			} else {
				throw new RuntimeException("Unable to execute request for '" + request + "'. Please verify " + serviceInfo.getName()
						+ " is working properly. Http Error Code: " + e.getStatusCode() + "-" + e.getStatusText(), e);
			}
		}
	}

	private HttpEntity<String> buildRequestHeader(final Credentials credentials) {
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + credentials.getEncoded());
		return new HttpEntity<>(headers);
	}

	private URI buildUri(final Service serviceId, final ServiceEndpoint endpoint) {
		return buildUri(serviceId, endpoint, emptySet());
	}

	private URI buildUri(final Service serviceId, final ServiceEndpoint endpoint, final Parameter... parameters) {
		return buildUri(serviceId, endpoint, asList(parameters));
	}

	private URI buildUri(final Service service, final ServiceEndpoint endpoint, final ParameterGroup... groups) {
		final Collection<Parameter> parameters = stream(groups).map(ParameterGroup::getParameters).flatMap(Arrays::stream).collect(Collectors.toList());
		return buildUri(service, endpoint, parameters);
	}

	private URI buildUri(final Service serviceId, final ServiceEndpoint endpoint, final Collection<Parameter> parameters) {
		final String serviceUrl = getServiceInfo(serviceInfo.getName()).getHomePageUrl();
		final String urlSuffix = urlSuffixBuilder.buildUrlSuffix(serviceId, endpoint, parameters);
		return URI.create(serviceUrl.substring(0, serviceUrl.length() - 1) + urlSuffix);
	}

	private InstanceInfo getServiceInfo(final String serviceName) {
		InstanceInfo instanceInfo = null;
		int tries = 0;
		while (instanceInfo == null && tries < MAX_INSTANCE_LOOKUP_ATTEMPTS) {
			try {
				instanceInfo = discoveryClient.getNextServerFromEureka(serviceName, false);
			} catch (final RuntimeException e) {
				if (e.getMessage().contains("No matches for the virtual host")) {
					LOG.error("Failed discovery of " + serviceInfo.getName() + ". Retrying " + (MAX_INSTANCE_LOOKUP_ATTEMPTS - tries - 1) + " more times.");
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
			LOG.info("Discovery of " + serviceInfo.getName() + " successful.");
		}
		return instanceInfo;
	}
}