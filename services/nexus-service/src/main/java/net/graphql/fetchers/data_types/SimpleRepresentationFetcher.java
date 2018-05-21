package net.graphql.fetchers.data_types;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.model.types.Selector;

public class SimpleRepresentationFetcher extends SelectorFetcher {

	public SimpleRepresentationFetcher(String type, ModelAdaptor adaptor) {
		super(type, adaptor);
	}

	@Override
	public Selector _get(DataFetchingEnvironment environment) {
		final Object simpleRepresentation = environment.getArgument("simpleRep");
		if (simpleRepresentation != null) {
			return adaptor.getDataTypeBySimpleRepresentation(type, simpleRepresentation.toString());
		}
		return super._get(environment);
	}
}