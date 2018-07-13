package net.thomas.portfolio.nexus.graphql.arguments;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLLong;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLList.list;
import static graphql.schema.GraphQLNonNull.nonNull;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLInputType;
import net.thomas.portfolio.nexus.graphql.arguments.extractors.FormattedDateExtractor;
import net.thomas.portfolio.nexus.graphql.arguments.extractors.UsageActivityTypeExtractor;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;

public enum GraphQlArgument {
	DOCUMENT_TYPE("documentType", nonNull(GraphQLString)),
	UID("uid", nonNull(GraphQLString)),
	OPTIONAL_UID("uid", GraphQLString),
	SIMPLE_REP("simpleRep", nonNull(GraphQLString)),
	OPTIONAL_SIMPLE_REP("simpleRep", GraphQLString),
	JUSTIFICATION("justification", GraphQLString),
	USER("user", GraphQLString),
	DATE_FORMAT_DETAIL_LEVEL("detailLevel", GraphQLString),
	DOCUMENT_TYPES("documentTypes", list(GraphQLString)),
	RELATIONS("relations", list(GraphQLString)),
	OFFSET("offset", GraphQLInt),
	LIMIT("limit", GraphQLInt),
	AFTER("after", GraphQLLong),
	AFTER_DATE("afterDate", GraphQLString, new FormattedDateExtractor()),
	BEFORE("before", GraphQLLong),
	BEFORE_DATE("beforeDate", GraphQLString, new FormattedDateExtractor()),
	TIME_OF_ACTIVITY("timeOfActivity", GraphQLLong),
	FORMATTED_TIME_OF_ACTIVITY("formattedTimeOfActivity", GraphQLString),
	USAGE_ACTIVITY_TYPE("activityType", nonNull(GraphQLString), new UsageActivityTypeExtractor());

	public static void initialize(Adaptors adaptors) {
		for (final GraphQlArgument argument : values()) {
			argument.extractor.initialize(adaptors);
		}
	}

	private final String name;
	private final GraphQLInputType type;
	private final ArgumentExtractor extractor;

	GraphQlArgument(String name, GraphQLInputType type) {
		this(name, type, new DefaultArgumentExtractor());
	}

	GraphQlArgument(String name, GraphQLInputType type, ArgumentExtractor extractor) {
		this.name = name;
		this.type = type;
		this.extractor = extractor;
	}

	public String getName() {
		return name;
	}

	public GraphQLInputType getType() {
		return type;
	}

	public boolean canBeExtractedFrom(DataFetchingEnvironment environment) {
		return environment.containsArgument(getName());
	}

	public <T> T extractFrom(DataFetchingEnvironment environment) {
		return extractor.extract(this, environment);
	}

	public static <T> T extractFirstThatIsPresent(DataFetchingEnvironment environment, GraphQlArgument... arguments) {
		for (final GraphQlArgument argument : arguments) {
			if (argument.canBeExtractedFrom(environment)) {
				return argument.extractFrom(environment);
			}
		}
		return null;
	}

	public static <T> T extractFirstThatIsPresent(DataFetchingEnvironment environment, T defaultValue, GraphQlArgument... arguments) {
		for (final GraphQlArgument argument : arguments) {
			if (argument.canBeExtractedFrom(environment)) {
				return argument.extractFrom(environment);
			}
		}
		return defaultValue;
	}

	private static class DefaultArgumentExtractor implements ArgumentExtractor {

		@Override
		public void initialize(Adaptors adaptors) {
		}

		@Override
		public <T> T extract(GraphQlArgument argument, DataFetchingEnvironment environment) {
			return environment.getArgument(argument.getName());
		}
	}
}