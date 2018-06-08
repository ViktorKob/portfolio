package net.thomas.portfolio.nexus.graphql.resolvers;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class DataTypeResolver implements TypeResolver {

	public DataTypeResolver(Adaptors adaptors) {
	}

	@Override
	public GraphQLObjectType getType(TypeResolutionEnvironment env) {
		final DataType documentInfo = (DataType) env.getObject();
		return env.getSchema()
			.getObjectType(documentInfo.getId().type);
	}

}
