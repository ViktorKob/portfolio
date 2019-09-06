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

import net.thomas.portfolio.common.services.parameters.Credentials;
import net.thomas.portfolio.common.services.parameters.ServiceDependency;

public class HttpRestClient {
	private static final Logger LOG = getLogger(HttpRestClient.class);
	private final RestTemplate restTemplate;
	private final ServiceDependency serviceInfo;

	public HttpRestClient(final RestTemplate restTemplate, final ServiceDependency serviceInfo) {
		this.restTemplate = restTemplate;
		this.serviceInfo = serviceInfo;
	}

	public <T> T loadUrlAsObject(String url, final HttpMethod method, final Class<T> responseType) {
		final URI request = URI.create(url);
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

	public <T> T loadUrlAsObject(String url, final HttpMethod method, final ParameterizedTypeReference<T> responseType) {
		final URI request = URI.create(url);
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
}