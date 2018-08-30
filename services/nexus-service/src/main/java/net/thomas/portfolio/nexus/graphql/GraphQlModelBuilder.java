package net.thomas.portfolio.nexus.graphql;

import static graphql.schema.GraphQLSchema.newSchema;

import javax.servlet.http.HttpServletRequest;

import graphql.schema.GraphQLObjectType;
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
		final GraphQLObjectType querySchema = new GraphQlQueryModelBuilder().setAdaptors(adaptors).build();
		final GraphQLObjectType mutationSchema = new GraphQlMutationModelBuilder().setAdaptors(adaptors).build();
		return new GraphQLSchemaProvider() {
			@Override
			public GraphQLSchema getSchema(final HttpServletRequest request) {
				return buildSchema(querySchema, mutationSchema);
			}

			@Override
			public GraphQLSchema getSchema() {
				return buildSchema(querySchema, mutationSchema);
			}

			@Override
			public GraphQLSchema getReadOnlySchema(final HttpServletRequest request) {
				return buildSchema(querySchema, mutationSchema);
			}

			private GraphQLSchema buildSchema(GraphQLObjectType querySchema, GraphQLObjectType mutationSchema) {
				return newSchema().query(querySchema).mutation(mutationSchema).build();
			}
		};
	}
}
