package net.thomas.common.services;

import static net.thomas.common.services.enums.Service.CLUSTER_DATA_SERVICE;
import static net.thomas.common.services.enums.Service.RENDER_SERVICE;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import net.thomas.common.services.enums.Service;
import net.thomas.common.services.enums.ServiceEndpoint;

public class RestHelper {
	private static final Map<Service, String> SERVER_MAP;

	static {
		SERVER_MAP = new EnumMap<>(Service.class);
		SERVER_MAP.put(CLUSTER_DATA_SERVICE, "http://localhost:8110");
		SERVER_MAP.put(RENDER_SERVICE, "http://localhost:8120");
	}

	private static final ThreadLocal<RestTemplate> restTemplate = new ThreadLocal<RestTemplate>() {
		@Override
		public RestTemplate get() {
			return new RestTemplate();
		}
	};
	private final ParameterBasedGet parameterBasedGet;
	private String host;
	private Integer port;

	public RestHelper() {
		parameterBasedGet = new ParameterBasedGet();
		host = null;
		port = null;
	}

	public void setProxy(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public <T extends Object> T getForParameters(Service serviceId, ServiceEndpoint endpoint, Class<T> responseType, Parameter... parameters) {
		return parameterBasedGet.get(serviceId, endpoint, responseType, Arrays.asList(parameters));
	}

	public <T extends Object> T getForParameters(Service serviceId, ServiceEndpoint endpoint, Class<T> responseType, Collection<Parameter> parameters) {
		return parameterBasedGet.get(serviceId, endpoint, responseType, parameters);
	}

	public <T extends Object> T getForObjects(Service serviceId, ServiceEndpoint endpoint, Class<T> responseType, GetParametizableObject<?>... objects) {
		return getForObjects(serviceId, endpoint, responseType, Arrays.asList(objects));
	}

	public <T extends Object> T getForObjects(Service serviceId, ServiceEndpoint endpoint, Class<T> responseType,
			Collection<GetParametizableObject<?>> objects) {
		final Collection<Parameter> parameters = new LinkedList<>();
		for (final GetParametizableObject<?> object : objects) {
			parameters.addAll(object.getAsParameterCollection());
		}
		return parameterBasedGet.get(serviceId, endpoint, responseType, parameters);
	}

	private final class ParameterBasedGet {
		public <T extends Object> T get(Service serviceId, ServiceEndpoint endpoint, Class<T> responseType, Collection<Parameter> parameters) {
			final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(serviceId, endpoint));
			addParametersToBuilder(builder, parameters);
			return fetchData(builder.build(), responseType);
		}

		private void addParametersToBuilder(UriComponentsBuilder builder, Collection<Parameter> parameters) {
			for (final Parameter parameter : parameters) {
				builder.queryParam(parameter.getName(), parameter.getValues());
			}
		}

		private <T extends Object> T fetchData(UriComponents request, Class<T> responseType) {
			final HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
			final HttpEntity<?> entity = new HttpEntity<>(headers);
			System.out.println(request.encode().toUri());
			final ResponseEntity<T> responseEntity = restTemplate.get().exchange(request.encode().toUri(), HttpMethod.GET, entity, responseType);
			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				return responseEntity.getBody();
			} else {
				throw new RuntimeException("Failed loading: " + request.toUri());
			}
		}
	}

	private String buildUrl(Service serviceId, ServiceEndpoint endpoint) {
		if (host == null) {
			return SERVER_MAP.get(serviceId) + serviceId.getPath() + endpoint.getPath();
		} else {
			return "http://" + host + ":" + port + serviceId.getPath() + endpoint.getPath();
		}
	}
}
