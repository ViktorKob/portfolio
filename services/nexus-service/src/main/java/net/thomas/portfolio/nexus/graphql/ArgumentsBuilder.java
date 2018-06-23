package net.thomas.portfolio.nexus.graphql;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLLong;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLList.list;
import static graphql.schema.GraphQLNonNull.nonNull;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import graphql.schema.GraphQLArgument;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;

public class ArgumentsBuilder {
	private static final boolean NULL_ALLOWED = false;
	private final List<GraphQLArgument> arguments;

	public ArgumentsBuilder() {
		arguments = new LinkedList<>();
	}

	public ArgumentsBuilder addDocumentType(Collection<String> documentTypes) {
		arguments.add(newArgument().name("documentType")
			.description("Document type from the model (from the set " + buildPresentationListFromCollection(documentTypes) + " )")
			.type(nonNull(GraphQLString))
			.build());
		return this;
	}

	public ArgumentsBuilder addUid() {
		return addUid(NULL_ALLOWED);
	}

	public ArgumentsBuilder addUid(boolean notNull) {
		arguments.add(newArgument().name("uid")
			.description("Unique id for entity")
			.type(notNull ? nonNull(GraphQLString) : GraphQLString)
			.build());
		return this;
	}

	public ArgumentsBuilder addSimpleRep() {
		arguments.add(newArgument().name("simpleRep")
			.description("Simple representation for selector")
			.type(GraphQLString)
			.build());
		return this;
	}

	public ArgumentsBuilder addJustification() {
		arguments.add(newArgument().name("justification")
			.description("Justification for executing query")
			.type(GraphQLString)
			.build());
		return this;
	}

	public ArgumentsBuilder addUser() {
		arguments.add(newArgument().name("user")
			.description("ID of the user trying to execute the query")
			.type(GraphQLString)
			.build());
		return this;
	}

	public ArgumentsBuilder addFormat() {
		arguments.add(newArgument().name("detailLevel")
			.description("Date rendering detail level; use 'dateOnly' to only render year-month-date or leave it out for date and time")
			.type(GraphQLString)
			.build());
		return this;
	}

	public ArgumentsBuilder addDocumentTypes(Collection<String> documentTypes) {
		arguments.add(newArgument().name("documentTypes")
			.description("Document types that should be included in the response (from the set " + buildPresentationListFromCollection(documentTypes) + " )")
			.type(list(GraphQLString))
			.build());
		return this;
	}

	public ArgumentsBuilder addRelations(Collection<String> relationTypes) {
		final String relationTypeList = buildPresentationListFromCollection(relationTypes);
		arguments.add(newArgument().name("relations")
			.description("Relation types that should be included in the response (from the set " + relationTypeList + " )")
			.type(list(GraphQLString))
			.build());
		return this;
	}

	public ArgumentsBuilder addPaging() {
		arguments.add(newArgument().name("offset")
			.description("Index of first element in result to include")
			.type(GraphQLInt)
			.defaultValue(0)
			.build());
		arguments.add(newArgument().name("limit")
			.description("Number of elements from result to include")
			.type(GraphQLInt)
			.defaultValue(20)
			.build());
		return this;
	}

	public ArgumentsBuilder addDateBounds() {
		arguments.add(newArgument().name("after")
			.description("Lower bound in milliseconds since the epoch")
			.type(GraphQLLong)
			.build());
		arguments.add(newArgument().name("before")
			.description("Upper bound in milliseconds since the epoch")
			.type(GraphQLLong)
			.build());
		arguments.add(newArgument().name("afterDate")
			.description("Lower bound formatted date in IEC 8601, e.g. '2017-11-23' or '2017-11-23T12:34:56+0200'")
			.type(GraphQLString)
			.build());
		arguments.add(newArgument().name("beforeDate")
			.description("Upper bound formatted date in IEC 8601, e.g. '2017-11-23' or '2017-11-23T12:34:56+0200'")
			.type(GraphQLString)
			.build());
		return this;
	}

	public ArgumentsBuilder addTimeOfActivity() {
		arguments.add(newArgument().name("timeOfActivity")
			.description("Upper bound in milliseconds since the epoch")
			.type(GraphQLLong)
			.build());
		arguments.add(newArgument().name("formattedTimeOfActivity")
			.description("Upper bound formatted date in IEC 8601, e.g. '2017-11-23' or '2017-11-23T12:34:56+0200'")
			.type(GraphQLString)
			.build());
		return this;
	}

	public ArgumentsBuilder addUsageActivityType() {
		arguments.add(newArgument().name("activityType")
			.description("What activity the user performed (from the set " + buildPresentationListFromArray(UsageActivityType.values()) + " )")
			.type(nonNull(GraphQLString))
			.build());
		return this;
	}

	public List<GraphQLArgument> build() {
		return arguments;
	}

	private String buildPresentationListFromCollection(Collection<String> values) {
		final String listOfValues = "[ " + values.stream()
			.sorted()
			.collect(joining(", ")) + " ]";
		return listOfValues;
	}

	private String buildPresentationListFromArray(Object[] values) {
		final String listOfValues = "[ " + Arrays.stream(values)
			.sorted()
			.map(Object::toString)
			.collect(joining(", ")) + " ]";
		return listOfValues;
	}
}
