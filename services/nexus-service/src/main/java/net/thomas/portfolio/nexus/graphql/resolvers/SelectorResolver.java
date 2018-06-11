package net.thomas.portfolio.nexus.graphql.resolvers;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class SelectorResolver implements TypeResolver {

	public SelectorResolver(Adaptors adaptors) {
	}

	@Override
	public GraphQLObjectType getType(TypeResolutionEnvironment env) {
		final DataTypeId selectorId = (DataTypeId) env.getObject();
		return env.getSchema()
			.getObjectType(selectorId.type);
	}

}
