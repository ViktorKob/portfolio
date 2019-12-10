package net.thomas.portfolio.service_commons.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.BANDWIDTH_LIMIT_EXCEEDED;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import net.thomas.portfolio.common.services.parameters.Credentials;
import net.thomas.portfolio.common.services.parameters.ServiceDependency;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class HttpRestClientUnitTest {
	private static final String SOME_RESULT = "SomeResult";
	private static final String SOME_URL_STRING = "http://localhost:8080/context/path";
	private static final ParameterizedTypeReference<String> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
	};

	private ResponseEntity<String> response;
	private RestTemplate restTemplate;
	private ServiceDependency serviceDependency;
	private HttpRestClient client;

	@Before
	public void setUp() {
		response = mock(ResponseEntity.class);
		when(response.getBody()).thenReturn(SOME_RESULT);
		when(response.getStatusCode()).thenReturn(OK);
		restTemplate = mock(RestTemplate.class);
		when(restTemplate.exchange(any(), any(), any(), (ParameterizedTypeReference) any())).thenReturn(response);
		serviceDependency = new ServiceDependency("service", new Credentials("user", "password"));
		client = new HttpRestClient(restTemplate, serviceDependency);
	}

	@Test
	public void shouldBuildUriInstance() {
		client.loadUrlAsObject(SOME_URL_STRING, GET);
		verify(restTemplate, times(1)).exchange(argThat(matches(SOME_URL_STRING)), eq(GET), any(), (ParameterizedTypeReference) any());
	}

	@Test
	public void shouldGetResponseAfterRequest() {
		final String result = client.loadUrlAsObject(SOME_URL_STRING, GET);
		assertEquals(SOME_RESULT, result);
	}

	@Test
	public void shouldGetResponseAfterRequestWithExplicitResponseType() {
		final String result = client.loadUrlAsObject(SOME_URL_STRING, GET, RESPONSE_TYPE);
		assertEquals(SOME_RESULT, result);
	}

	@Test
	public void shouldGetBooleanWhenResourceCreated() {
		when(response.getStatusCode()).thenReturn(CREATED);
		final Boolean success = client.loadUrlAsObject(SOME_URL_STRING, POST);
		assertTrue(success);
	}

	@Test(expected = UnableToCompleteRequestException.class)
	public void shouldAssumeFailureWhenOtherStatusIsReceived() {
		when(response.getStatusCode()).thenReturn(CONFLICT);
		client.loadUrlAsObject(SOME_URL_STRING, GET);
	}

	@Test
	public void shouldReturnNullWhenResourceIsNotFound() {
		when(restTemplate.exchange(any(), any(), any(), (ParameterizedTypeReference) any())).thenThrow(new HttpClientErrorException(NOT_FOUND));
		final String result = client.loadUrlAsObject(SOME_URL_STRING, GET);
		assertNull(result);
	}

	@Test(expected = UnauthorizedAccessException.class)
	public void shouldThrowUnauthorizedAccessExceptionWhenUnauthorized() {
		when(restTemplate.exchange(any(), any(), any(), (ParameterizedTypeReference) any())).thenThrow(new HttpClientErrorException(UNAUTHORIZED));
		client.loadUrlAsObject(SOME_URL_STRING, GET);
	}

	@Test(expected = BadRequestException.class)
	public void shouldThrowBadRequestExceptionWhenRequestIsBad() {
		when(restTemplate.exchange(any(), any(), any(), (ParameterizedTypeReference) any())).thenThrow(new HttpClientErrorException(BAD_REQUEST));
		client.loadUrlAsObject(SOME_URL_STRING, GET);
	}

	@Test(expected = UnableToCompleteRequestException.class)
	public void shouldThrowGenericExceptionWhenRequestFailsForOtherReason() {
		when(restTemplate.exchange(any(), any(), any(), (ParameterizedTypeReference) any())).thenThrow(new HttpClientErrorException(BANDWIDTH_LIMIT_EXCEEDED));
		client.loadUrlAsObject(SOME_URL_STRING, GET);
	}

	private ArgumentMatcher<URI> matches(String urlString) {
		return uri -> uri.toString().equals(urlString);
	}
}
