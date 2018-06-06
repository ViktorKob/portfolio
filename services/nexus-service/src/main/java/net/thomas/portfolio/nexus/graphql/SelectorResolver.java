package net.thomas.portfolio.nexus.graphql;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;

public class SelectorResolver implements TypeResolver {

	public SelectorResolver(Adaptors adaptors) {
	}

	@Override
	public GraphQLObjectType getType(TypeResolutionEnvironment env) {
		return null;
	}

}
