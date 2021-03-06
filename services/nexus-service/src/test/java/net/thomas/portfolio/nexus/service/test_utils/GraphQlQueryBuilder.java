package net.thomas.portfolio.nexus.service.test_utils;

import static net.thomas.portfolio.common.services.parameters.ParameterGroup.asGroup;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlQueryBuilder.QueryType.MUTATION;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlQueryBuilder.QueryType.QUERY;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.SingleParameter;
import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument;

public class GraphQlQueryBuilder {
	private QueryType type;
	private final Map<String, String> variables;
	private String query;

	public GraphQlQueryBuilder() {
		variables = new HashMap<>();
		type = QUERY;
	}

	public void markAsMutation() {
		type = MUTATION;
	}

	public GraphQlQueryBuilder setNothingToFieldValueQuery(final String dataType, final String fieldPath) {
		query = "test{" + dataType + "{" + fieldPath + "}}";
		return this;
	}

	public GraphQlQueryBuilder setUidToFieldValueQuery(final String dataType, final String fieldPath) {
		query = "test($uid:String){" + dataType + "(uid:$uid){" + fieldPath + "}}";
		return this;
	}

	public GraphQlQueryBuilder setUidAndUserToFieldValueQuery(final String dataType, final String fieldPath) {
		query = "test($uid:String,$user:String){" + dataType + "(uid:$uid,user:$user){" + fieldPath + "}}";
		return this;
	}

	public GraphQlQueryBuilder setSimpleRepToFieldValueQuery(final String dataType, final String fieldPath) {
		query = "test($simpleRepresentation:String){" + dataType + "(simpleRep:$simpleRepresentation){" + fieldPath + "}}";
		return this;
	}

	public GraphQlQueryBuilder setUidActivityAndDocumentTypeToUsageActivityMutation(final String documentType, final String fieldPath) {
		query = "test($uid: String!,$activityType: String!,$user: String){usageActivity{" + documentType
				+ "(uid:$uid) {add(user:$user,activityType:$activityType){" + fieldPath + "}}}}";
		return this;
	}

	public GraphQlQueryBuilder setUidAndUsageActivityArgumentToFieldValueQuery(final String dataType, final GraphQlArgument argument, Object value,
			final String fieldPath) {
		if (value instanceof String) {
			value = "\"" + value + "\"";
		}
		query = "test($uid:String,$user:String){" + dataType + "(uid:$uid,user:$user){usageActivities(" + argument.getName() + ":" + value + "){" + fieldPath
				+ "}}}";
		return this;
	}

	public GraphQlQueryBuilder setSuggestionsToSelectorsQuery() {
		query = " test($simpleRepresentation:String!){suggest(simpleRep:$simpleRepresentation){uid}}";
		return this;
	}

	public GraphQlQueryBuilder addVariable(final String name, final Object value) {
		variables.put(name, value.toString());
		return this;
	}

	public ParameterGroup build() {
		switch (type) {
			case MUTATION:
				query = "mutation " + query;
				break;
			case QUERY:
			default:
				query = "query " + query;
				break;
		}
		return asGroup(new SingleParameter("query", query), new SingleParameter("operationName", "test"), jsonParameter("variables", variables));
	}

	private SingleParameter jsonParameter(final String variable, final Map<String, String> value) {
		try {
			return new SingleParameter(variable, new ObjectMapper().writeValueAsString(value));
		} catch (final JsonProcessingException cause) {
			throw new QueryBuildException("Parameter creation failed", cause);
		}
	}

	enum QueryType {
		QUERY,
		MUTATION;
	}

	public static class QueryBuildException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public QueryBuildException(String message) {
			super(message);
		}

		public QueryBuildException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}