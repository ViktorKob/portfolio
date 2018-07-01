package net.thomas.portfolio.nexus.graphql.arguments;

import static graphql.schema.GraphQLArgument.newArgument;
import static java.util.stream.Collectors.joining;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.AFTER;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.AFTER_DATE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.BEFORE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.BEFORE_DATE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.DATE_FORMAT_DETAIL_LEVEL;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.DOCUMENT_TYPE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.DOCUMENT_TYPES;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.FORMATTED_TIME_OF_ACTIVITY;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.JUSTIFICATION;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.LIMIT;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.OFFSET;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.OPTIONAL_SIMPLE_REP;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.OPTIONAL_UID;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.RELATIONS;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.SIMPLE_REP;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.TIME_OF_ACTIVITY;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.UID;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.USAGE_ACTIVITY_TYPE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.USER;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import graphql.schema.GraphQLArgument;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;

public class ArgumentsBuilder {
	private final List<GraphQLArgument> arguments;

	public ArgumentsBuilder() {
		arguments = new LinkedList<>();
	}

	public ArgumentsBuilder addDocumentType(Collection<String> documentTypes) {
		final String description = "Document type from the model (from the set " + buildPresentationListFromCollection(documentTypes) + " )";
		arguments.add(buildArgument(DOCUMENT_TYPE, description));
		return this;
	}

	public ArgumentsBuilder addUid(boolean optional) {
		final String description = "Unique id for entity";
		arguments.add(buildArgument(optional ? OPTIONAL_UID : UID, description));
		return this;
	}

	public ArgumentsBuilder addSimpleRep(boolean optional) {
		arguments.add(buildArgument(optional ? OPTIONAL_SIMPLE_REP : SIMPLE_REP, "Simple representation for the selector"));
		return this;
	}

	public ArgumentsBuilder addJustification() {
		arguments.add(buildArgument(JUSTIFICATION, "Justification for executing query"));
		return this;
	}

	public ArgumentsBuilder addUser() {
		arguments.add(buildArgument(USER, "ID of the user trying to execute the query"));
		return this;
	}

	public ArgumentsBuilder addFormat() {
		arguments.add(buildArgument(DATE_FORMAT_DETAIL_LEVEL,
				"Date rendering detail level; use 'dateOnly' to only render year-month-date or leave it out for date and time"));
		return this;
	}

	public ArgumentsBuilder addDocumentTypes(Collection<String> documentTypes) {
		arguments.add(buildArgument(DOCUMENT_TYPES,
				"Document types that should be included in the response (from the set " + buildPresentationListFromCollection(documentTypes) + " )"));
		return this;
	}

	public ArgumentsBuilder addRelations(Collection<String> relationTypes) {
		final String relationTypeList = buildPresentationListFromCollection(relationTypes);
		arguments.add(buildArgument(RELATIONS, "Relation types that should be included in the response (from the set " + relationTypeList + " )"));
		return this;
	}

	public ArgumentsBuilder addPaging() {
		arguments.add(buildArgument(OFFSET, "Index of first element in result to include", 0));
		arguments.add(buildArgument(LIMIT, "Number of elements from result to include", 20));
		return this;
	}

	public ArgumentsBuilder addDateBounds() {
		arguments.add(buildArgument(AFTER, "Lower bound in milliseconds since the epoch"));
		arguments.add(buildArgument(BEFORE, "Upper bound in milliseconds since the epoch"));
		arguments.add(buildArgument(AFTER_DATE, "Lower bound formatted date in IEC 8601, e.g. '2017-11-23' or '2017-11-23T12:34:56+0200'"));
		arguments.add(buildArgument(BEFORE_DATE, "Upper bound formatted date in IEC 8601, e.g. '2017-11-23' or '2017-11-23T12:34:56+0200'"));
		return this;
	}

	public ArgumentsBuilder addTimeOfActivity() {
		arguments.add(buildArgument(TIME_OF_ACTIVITY, "Upper bound in milliseconds since the epoch"));
		arguments.add(buildArgument(FORMATTED_TIME_OF_ACTIVITY, "Upper bound formatted date in IEC 8601, e.g. '2017-11-23' or '2017-11-23T12:34:56+0200'"));
		return this;
	}

	public ArgumentsBuilder addUsageActivityType() {
		final String description = "What activity the user performed (from the set " + buildPresentationListFromArray(UsageActivityType.values()) + " )";
		arguments.add(buildArgument(USAGE_ACTIVITY_TYPE, description));
		return this;
	}

	public List<GraphQLArgument> build() {
		return arguments;
	}

	private GraphQLArgument buildArgument(GraphQlArgument argument, String description) {
		return newArgument().name(argument.getName())
			.description(description)
			.type(argument.getType())
			.build();
	}

	private <T> GraphQLArgument buildArgument(GraphQlArgument argument, String description, T defaultValue) {
		return newArgument().name(argument.getName())
			.description(description)
			.type(argument.getType())
			.defaultValue(defaultValue)
			.build();
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
