package net.thomas.portfolio.service_commons.network;

import static java.lang.System.nanoTime;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.net.URI;

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
import net.thomas.portfolio.services.ContextPathSection;
import net.thomas.portfolio.services.Service;

public class HttpRestClient {
	private static final Logger LOG = getLogger(HttpRestClient.class);
	private static final int MAX_INSTANCE_LOOKUP_ATTEMPTS = 60;
	private final EurekaClient discoveryClient;
	private final RestTemplate restTemplate;
	private final ServiceDependency serviceInfo;
	private final UrlFactory urlFactory;

	public HttpRestClient(final EurekaClient discoveryClient, final RestTemplate restTemplate, final ServiceDependency serviceInfo) {
		this.discoveryClient = discoveryClient;
		this.restTemplate = restTemplate;
		this.serviceInfo = serviceInfo;
		urlFactory = new UrlFactory(() -> {
			final String serviceUrl = getServiceInfo(serviceInfo.getName()).getHomePageUrl();
			return serviceUrl.substring(0, serviceUrl.length() - 1);
		}, new PortfolioUrlSuffixBuilder());
	}

	public <T> T loadUrlAsObject(final Service service, final ContextPathSection endpoint, final HttpMethod method, final Class<T> responseType) {
		final URI request = URI.create(urlFactory.buildUrl(service, endpoint));
		return execute(request, method, responseType);
	}

	public <T> T loadUrlAsObject(final Service service, final ContextPathSection endpoint, final HttpMethod method, final Class<T> responseType,
			final ParameterGroup... parameters) {
		final URI request = URI.create(urlFactory.buildUrl(service, endpoint, parameters));
		return execute(request, method, responseType);
	}

	public <T> T loadUrlAsObject(final Service service, final ContextPathSection endpoint, final HttpMethod method, final Class<T> responseType,
			final Parameter... parameters) {
		final URI request = URI.create(urlFactory.buildUrl(service, endpoint, parameters));
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

	public <T> T loadUrlAsObject(final Service service, final ContextPathSection endpoint, final HttpMethod method,
			final ParameterizedTypeReference<T> responseType, final ParameterGroup... parameters) {
		final URI request = URI.create(urlFactory.buildUrl(service, endpoint, parameters));
		return execute(request, method, responseType);
	}

	public <T> T loadUrlAsObject(final Service service, final ContextPathSection endpoint, final HttpMethod method,
			final ParameterizedTypeReference<T> responseType, final Parameter... parameters) {
		final URI request = URI.create(urlFactory.buildUrl(service, endpoint, parameters));
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