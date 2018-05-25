package net.thomas.portfolio.graphql.fetchers.data_types;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SimpleRepresentationFetcher extends SelectorFetcher {

	public SimpleRepresentationFetcher(String type, Adaptors adaptors) {
		super(type, adaptors);
	}

	@Override
	public Selector _get(DataFetchingEnvironment environment) {
		final Object simpleRepresentation = environment.getArgument("simpleRep");
		if (simpleRepresentation != null) {
			final DataTypeId id = adaptors.getIdFromSimpleRep(type, simpleRepresentation.toString());
			return (Selector) adaptors.getDataType(id);
		}
		return super._get(environment);
	}
}