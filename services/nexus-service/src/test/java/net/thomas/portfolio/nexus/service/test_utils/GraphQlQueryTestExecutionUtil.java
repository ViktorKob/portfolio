package net.thomas.portfolio.nexus.service.test_utils;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static net.thomas.portfolio.services.Service.NEXUS_SERVICE;
import static org.springframework.http.HttpMethod.GET;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;

import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.services.ServiceEndpoint;

public class GraphQlQueryTestExecutionUtil {
	private static final ServiceEndpoint GRAPHQL = () -> {
		return "/graphql";
	};
	private static final ParameterizedTypeReference<LinkedHashMap<String, Object>> JSON = new ParameterizedTypeReference<LinkedHashMap<String, Object>>() {
	};
	private final HttpRestClient client;

	public GraphQlQueryTestExecutionUtil(final HttpRestClient client) {
		this.client = client;
	}

	@SuppressWarnings("unchecked")
	public <T> T executeQueryAndLookupResponseAtPath(final ParameterGroup query, final String... path) {
		final Map<String, Object> response = executeQuery(query);
		return (T) lookupFirstValidReponseElement(response, path);
	}

	private Map<String, Object> executeQuery(final ParameterGroup parameterGroup) {
		return client.loadUrlAsObject(NEXUS_SERVICE, GRAPHQL, GET, JSON, parameterGroup);
	}

	@SuppressWarnings("unchecked")
	public Object lookupFirstValidReponseElement(final Map<String, Object> response, final String... path) {
		try {
			Object result = response;
			for (final String element : path) {
				while (result instanceof List) {
					result = ((List<?>) result).get(0);
				}
				result = ((Map<String, Object>) result).get(element);
			}
			return result;
		} catch (final Exception e) {
			throw new RuntimeException("Unable to lookup path " + stream(path).collect(joining(".")) + " in response: " + response, e);
		}
	}
}