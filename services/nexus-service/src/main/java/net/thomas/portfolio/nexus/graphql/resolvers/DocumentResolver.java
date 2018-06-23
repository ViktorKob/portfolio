package net.thomas.portfolio.nexus.graphql.resolvers;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import net.thomas.portfolio.nexus.graphql.fetchers.data_proxies.DocumentInfoProxy;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;

public class DocumentResolver implements TypeResolver {

	public DocumentResolver(Adaptors adaptors) {
	}

	@Override
	public GraphQLObjectType getType(TypeResolutionEnvironment env) {
		final Object object = env.getObject();
		if (object instanceof DocumentInfoProxy) {
			final DocumentInfoProxy proxy = (DocumentInfoProxy) object;
			return env.getSchema()
				.getObjectType(proxy.getId().type);
		}
		return null;
	}
}