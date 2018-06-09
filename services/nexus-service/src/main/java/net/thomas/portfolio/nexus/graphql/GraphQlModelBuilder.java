package net.thomas.portfolio.nexus.graphql;

import static graphql.schema.GraphQLSchema.newSchema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;

public class GraphQlModelBuilder {
	private Adaptors adaptors;

	public GraphQlModelBuilder() {
	}

	public GraphQlModelBuilder setAdaptor(Adaptors adaptors) {
		this.adaptors = adaptors;
		return this;
	}

	public GraphQLSchema build() {
		final GraphQLObjectType queryModel = new GraphQlQueryModelBuilder().setAdaptors(adaptors)
			.build();
		// GraphQLObjectType mutationModel = new GraphQlMutationModelBuilder().setAdaptors(adaptors)
		// .build();
		return newSchema().query(queryModel)
			// .mutation(mutationModel)
			.build();
	}
}
