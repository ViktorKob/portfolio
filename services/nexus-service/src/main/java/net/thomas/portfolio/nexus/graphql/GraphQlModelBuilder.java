package net.thomas.portfolio.nexus.graphql;

import graphql.schema.GraphQLSchema;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;

public class GraphQlModelBuilder {
	private Adaptors adaptors;

	public GraphQlModelBuilder() {
	}

	public GraphQlModelBuilder setAdaptors(final Adaptors adaptors) {
		this.adaptors = adaptors;
		return this;
	}

	public GraphQLSchema build() {
		return GraphQLSchema.newSchema()
				.query(new GraphQlQueryModelBuilder().setAdaptors(adaptors).build())
				.mutation(new GraphQlMutationModelBuilder().setAdaptors(adaptors).build())
				.build();
	}
}
