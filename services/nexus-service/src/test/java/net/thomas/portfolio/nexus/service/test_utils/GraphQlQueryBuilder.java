package net.thomas.portfolio.nexus.service.test_utils;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.PreSerializedParameter;

public class GraphQlQueryBuilder {
	private final Map<String, String> variables;
	private String query;

	public GraphQlQueryBuilder() {
		variables = new HashMap<>();
	}

	public GraphQlQueryBuilder setUidToFieldValueQuery(String dataType, String fieldName) {
		query = "query test($uid:String){" + dataType + "(uid:$uid) {" + fieldName + "}}";
		return this;
	}

	public GraphQlQueryBuilder setSimpleRepToFieldValueQuery(String dataType, String fieldName) {
		query = "query test($simpleRepresentation:String){" + dataType + "(simpleRep:$simpleRepresentation) {" + fieldName + "}}";
		return this;
	}

	public GraphQlQueryBuilder addVariable(String name, String value) {
		variables.put(name, value);
		return this;
	}

	public ParameterGroup build() {
		return new ParameterGroup() {
			@Override
			public Parameter[] getParameters() {
				return new Parameter[] { new PreSerializedParameter("query", query), new PreSerializedParameter("operationName", "test"),
						jsonParameter("variables", variables) };
			}
		};
	}

	private PreSerializedParameter jsonParameter(String variable, Map<String, String> value) {
		try {
			return new PreSerializedParameter(variable, new ObjectMapper().writeValueAsString(value));
		} catch (final JsonProcessingException e) {
			throw new RuntimeException("Parameter creation failed", e);
		}
	}
}