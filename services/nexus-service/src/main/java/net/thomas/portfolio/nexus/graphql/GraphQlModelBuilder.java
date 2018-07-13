package net.thomas.portfolio.nexus.graphql;

import static graphql.schema.GraphQLSchema.newSchema;

import graphql.schema.GraphQLSchema;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;

public class GraphQlModelBuilder {
	private Adaptors adaptors;

	public GraphQlModelBuilder() {
	}

	public GraphQlModelBuilder setAdaptors(Adaptors adaptors) {
		this.adaptors = adaptors;
		return this;
	}

	public GraphQLSchema build() {
		final GraphQlQueryModelBuilder queryModel = new GraphQlQueryModelBuilder().setAdaptors(adaptors);
		final GraphQlMutationModelBuilder mutationModel = new GraphQlMutationModelBuilder().setAdaptors(adaptors);
		return newSchema().query(queryModel.build())
			.mutation(mutationModel.build())
			.build();
	}
}
