package net.thomas.portfolio.nexus.service.test_utils;

import static net.thomas.portfolio.common.services.parameters.ParameterGroup.asGroup;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.PreSerializedParameter;

public class GraphQlQueryBuilder {
	private final Map<String, String> variables;
	private String query;

	public GraphQlQueryBuilder() {
		variables = new HashMap<>();
	}

	public GraphQlQueryBuilder setNothingToFieldValueQuery(String dataType, String fieldName) {
		query = "query test{" + dataType + "{" + fieldName + "}}";
		return this;
	}

	public GraphQlQueryBuilder setUidToFieldValueQuery(String dataType, String fieldName) {
		query = "query test($uid:String){" + dataType + "(uid:$uid) {" + fieldName + "}}";
		return this;
	}

	public GraphQlQueryBuilder setSimpleRepToFieldValueQuery(String dataType, String fieldName) {
		query = "query test($simpleRepresentation:String){" + dataType + "(simpleRep:$simpleRepresentation) {" + fieldName + "}}";
		return this;
	}

	public GraphQlQueryBuilder setSuggestionsToSelectorsQuery() {
		query = "query test($simpleRepresentation:String!){suggest(simpleRep:$simpleRepresentation){uid}}";
		return this;
	}

	public GraphQlQueryBuilder addVariable(String name, String value) {
		variables.put(name, value);
		return this;
	}

	public ParameterGroup build() {
		return asGroup(new PreSerializedParameter("query", query), new PreSerializedParameter("operationName", "test"), jsonParameter("variables", variables));
	}

	private PreSerializedParameter jsonParameter(String variable, Map<String, String> value) {
		try {
			return new PreSerializedParameter(variable, new ObjectMapper().writeValueAsString(value));
		} catch (final JsonProcessingException e) {
			throw new RuntimeException("Parameter creation failed", e);
		}
	}
}