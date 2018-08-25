package net.thomas.portfolio.nexus.graphql;

import static graphql.schema.GraphQLSchema.newSchema;

import javax.servlet.http.HttpServletRequest;

import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLSchemaProvider;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;

public class GraphQlModelBuilder {
	private Adaptors adaptors;

	public GraphQlModelBuilder() {
	}

	public GraphQlModelBuilder setAdaptors(final Adaptors adaptors) {
		this.adaptors = adaptors;
		return this;
	}

	public GraphQLSchemaProvider build() {
		final GraphQlQueryModelBuilder queryModel = new GraphQlQueryModelBuilder().setAdaptors(adaptors);
		final GraphQlMutationModelBuilder mutationModel = new GraphQlMutationModelBuilder().setAdaptors(adaptors);
		return new GraphQLSchemaProvider() {
			@Override
			public GraphQLSchema getSchema(final HttpServletRequest request) {
				return buildSchema(queryModel, mutationModel);
			}

			@Override
			public GraphQLSchema getSchema() {
				return buildSchema(queryModel, mutationModel);
			}

			@Override
			public GraphQLSchema getReadOnlySchema(final HttpServletRequest request) {
				return buildSchema(queryModel, mutationModel);
			}

			private GraphQLSchema buildSchema(final GraphQlQueryModelBuilder queryModel, final GraphQlMutationModelBuilder mutationModel) {
				return newSchema().query(queryModel.build()).mutation(mutationModel.build()).build();
			}
		};
	}
}
