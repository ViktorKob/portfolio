package net.thomas.portfolio.nexus.graphql;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;

public class DocumentResolver implements TypeResolver {

	private final Adaptors adaptors;

	public DocumentResolver(Adaptors adaptors) {
		this.adaptors = adaptors;
	}

	@Override
	public GraphQLObjectType getType(TypeResolutionEnvironment env) {
		if (env.getObject() instanceof DocumentInfo) {
			final DocumentInfo documentInfo = (DocumentInfo) env.getObject();
			return env.getSchema()
				.getObjectType(documentInfo.getId().type);
		}
		return null;
	}
}