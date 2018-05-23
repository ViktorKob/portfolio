package net.thomas.portfolio.graphql.fetchers.data_types;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public class SimpleRepresentationFetcher extends SelectorFetcher {

	public SimpleRepresentationFetcher(String type, Adaptors adaptors) {
		super(type, adaptors);
	}

	@Override
	public Selector _get(DataFetchingEnvironment environment) {
		final Object simpleRepresentation = environment.getArgument("simpleRep");
		if (simpleRepresentation != null) {
			return adaptors.getDataTypeBySimpleRepresentation(type, simpleRepresentation.toString());
		}
		return super._get(environment);
	}
}