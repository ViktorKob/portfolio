package net.thomas.portfolio.nexus.service.test_utils;

import static net.thomas.portfolio.common.services.parameters.ParameterGroup.asGroup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.PreSerializedParameter;

public class NewGraphQlQueryBuilder {
	private final Map<String, String> variables;
	private final Stack<QueryLevel> queryStack;

	public static class QueryLevel {
		public final List<QueryParameter> parameters;
		public Map<String, QueryField> fields;

		public QueryLevel() {
			parameters = new LinkedList<>();
			fields = new HashMap<>();
		}
	}

	public NewGraphQlQueryBuilder() {
		variables = new HashMap<>();
		queryStack = new Stack<>();

	}

	public NewGraphQlQueryBuilder addUsageActivityTopLayer() {
		final QueryLevel level = new QueryLevel();
		level.parameters.add(new QueryParameter("uid", "String!"));
		level.parameters.add(new QueryParameter("activityType", "String!"));
		queryStack.push(level);
		return this;
	}

	public ParameterGroup build() {
		return asGroup(new PreSerializedParameter("query", buildMutationQuery()), new PreSerializedParameter("operationName", "test"),
				jsonParameter("variables", variables));
	}

	private String buildMutationQuery() {
		final StringBuilder builder = new StringBuilder();
		while (!queryStack.isEmpty()) {
			final QueryLevel level = queryStack.pop();
			builder.append(render(level, ""));
		}
		return builder.toString();
	}

	private String render(QueryLevel level, String contents) {
		final StringBuilder builder = new StringBuilder();
		for (final Entry<String, QueryField> field : level.fields.entrySet()) {
			field.getValue();
		}
		return builder.toString();
	}

	private PreSerializedParameter jsonParameter(String variable, Map<String, String> value) {
		try {
			return new PreSerializedParameter(variable, new ObjectMapper().writeValueAsString(value));
		} catch (final JsonProcessingException e) {
			throw new RuntimeException("Parameter creation failed", e);
		}
	}

	public static class QueryParameter {
		public String name;
		public String value;

		public QueryParameter(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	public static class QueryField {
		public String name;

		public QueryField(String name) {
			this.name = name;
		}
	}
}