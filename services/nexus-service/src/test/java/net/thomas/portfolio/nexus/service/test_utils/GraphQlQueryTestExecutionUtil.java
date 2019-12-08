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
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.services.ContextPathSection;

public class GraphQlQueryTestExecutionUtil {
	private static final ContextPathSection GRAPH_QL = () -> {
		return "/graphql";
	};
	private static final ParameterizedTypeReference<LinkedHashMap<String, Object>> JSON = new ParameterizedTypeReference<>() {
	};
	private final UrlFactory urlFactory;
	private final HttpRestClient client;

	public GraphQlQueryTestExecutionUtil(final UrlFactory urlFactory, final HttpRestClient client) {
		this.urlFactory = urlFactory;
		this.client = client;
	}

	@SuppressWarnings("unchecked")
	public <T> T executeQueryAndLookupResponseAtPath(final ParameterGroup query, final String... path) {
		final Map<String, Object> response = executeQuery(query);
		return (T) lookupFirstValidReponseElement(response, path);
	}

	@SuppressWarnings("unchecked")
	public <T> T executeMutationAndLookupResponseAtPath(final ParameterGroup query, final String... path) {
		final Map<String, Object> response = executeMutation(query);
		return (T) lookupFirstValidReponseElement(response, path);
	}

	private Map<String, Object> executeQuery(final ParameterGroup parameterGroup) {
		final String url = urlFactory.buildUrl(NEXUS_SERVICE, GRAPH_QL, parameterGroup);
		return client.loadUrlAsObject(url, GET, JSON);
	}

	private Map<String, Object> executeMutation(final ParameterGroup parameterGroup) {
		final String url = urlFactory.buildUrl(NEXUS_SERVICE, GRAPH_QL, parameterGroup);
		return client.loadUrlAsObject(url, GET, JSON);
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
		} catch (final Exception cause) {
			throw new InvalidGraphQlResponseException("Unable to lookup path " + stream(path).collect(joining(".")) + " in response: " + response, cause);
		}
	}

	public static class InvalidGraphQlResponseException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public InvalidGraphQlResponseException(String message) {
			super(message);
		}

		public InvalidGraphQlResponseException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}