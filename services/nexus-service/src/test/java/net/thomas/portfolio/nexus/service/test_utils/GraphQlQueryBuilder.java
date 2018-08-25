package net.thomas.portfolio.nexus.service.test_utils;

import static net.thomas.portfolio.common.services.parameters.ParameterGroup.asGroup;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlQueryBuilder.QueryType.MUTATION;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlQueryBuilder.QueryType.QUERY;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.PreSerializedParameter;

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
		query = "test($uid:String){" + dataType + "(uid:$uid) {" + fieldPath + "}}";
		return this;
	}

	public GraphQlQueryBuilder setUidAndUserToFieldValueQuery(final String dataType, final String fieldPath) {
		query = "test($uid:String,$user:String){" + dataType + "(uid:$uid,user:$user) {" + fieldPath + "}}";
		return this;
	}

	public GraphQlQueryBuilder setSimpleRepToFieldValueQuery(final String dataType, final String fieldPath) {
		query = "test($simpleRepresentation:String){" + dataType + "(simpleRep:$simpleRepresentation) {" + fieldPath + "}}";
		return this;
	}

	public GraphQlQueryBuilder setUidActivityAndDocumentTypeToUsageActivityMutation(final String documentType, final String fieldPath) {
		query = "test($uid: String!,$activityType: String!,$user: String){usageActivity{" + documentType
				+ "(uid:$uid) {add(user:$user,activityType:$activityType){" + fieldPath + "}}}}";
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
		return asGroup(new PreSerializedParameter("query", query), new PreSerializedParameter("operationName", "test"), jsonParameter("variables", variables));
	}

	private PreSerializedParameter jsonParameter(final String variable, final Map<String, String> value) {
		try {
			return new PreSerializedParameter(variable, new ObjectMapper().writeValueAsString(value));
		} catch (final JsonProcessingException e) {
			throw new RuntimeException("Parameter creation failed", e);
		}
	}

	enum QueryType {
		QUERY,
		MUTATION;
	}
}