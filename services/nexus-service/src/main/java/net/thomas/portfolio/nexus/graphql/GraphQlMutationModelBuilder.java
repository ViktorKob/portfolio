package net.thomas.portfolio.nexus.graphql;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import net.thomas.portfolio.nexus.graphql.fetchers.usage_data.UsageActivityDocumentMutationsFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.usage_data.UsageActivityDocumentsMutationsFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.usage_data.UsageActivityMutation;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;

public class GraphQlMutationModelBuilder {

	private static final boolean NOT_NULL = true;
	private Adaptors adaptors;

	public GraphQlMutationModelBuilder() {
	}

	public GraphQlMutationModelBuilder setAdaptors(Adaptors adaptors) {
		this.adaptors = adaptors;
		return this;
	}

	public GraphQLObjectType build() {
		final List<GraphQLFieldDefinition> fieldDefinitions = buildFieldDefinitions(adaptors);
		return new GraphQLObjectType("NexusMutationModel", "Model enabling modifications of all relevant sub-services as one data structure", fieldDefinitions,
				emptyList());
	}

	private List<GraphQLFieldDefinition> buildFieldDefinitions(Adaptors adaptors) {
		final LinkedList<GraphQLFieldDefinition> fields = new LinkedList<>();
		fields.add(newFieldDefinition().name("usageActivity")
			.description("Mutations on usage activities")
			.type(buildDocumentsMutationTypes(adaptors))
			.dataFetcher(new UsageActivityDocumentsMutationsFetcher(adaptors))
			.build());
		return fields;
	}

	private GraphQLOutputType buildDocumentsMutationTypes(Adaptors adaptors) {
		final ArgumentsBuilder arguments = new ArgumentsBuilder().addUid(NOT_NULL);
		final GraphQLObjectType.Builder builder = GraphQLObjectType.newObject()
			.name("UsageActivityMutations")
			.description("Collection of executable usage activity mutations on document types (from the set "
					+ buildPresentationListFromCollection(adaptors.getDocumentTypes()) + " )");
		for (final String documentType : adaptors.getDocumentTypes()) {
			builder.field(newFieldDefinition().name(documentType)
				.description("Mutations on usage activities for " + documentType)
				.argument(arguments.build())
				.type(buildDocumentMutationTypes(documentType, adaptors))
				.dataFetcher(new UsageActivityDocumentMutationsFetcher(documentType, adaptors))
				.build());
		}
		return builder.build();
	}

	private GraphQLOutputType buildDocumentMutationTypes(String documentType, Adaptors adaptors) {
		final GraphQLObjectType.Builder builder = GraphQLObjectType.newObject()
			.name(documentType + "_usageActivityMutations")
			.description("Collection of executable usage activity mutations on document type " + documentType);
		builder.field(createUsageActivityMutationField(adaptors));
		return builder.build();
	}

	private GraphQLFieldDefinition createUsageActivityMutationField(Adaptors adaptors) {
		final ArgumentsBuilder arguments = new ArgumentsBuilder().addUser()
			.addUsageActivityType()
			.addTimeOfActivity();
		return newFieldDefinition().name("add")
			.description("Register user interaction with this document")
			.argument(arguments.build())
			.type(new GraphQLTypeReference("UsageActivity"))
			.dataFetcher(new UsageActivityMutation(adaptors))
			.build();
	}

	private String buildPresentationListFromCollection(Collection<String> values) {
		final String listOfValues = "[ " + values.stream()
			.sorted()
			.collect(joining(", ")) + " ]";
		return listOfValues;
	}
}