package net.thomas.portfolio.graphql.fetchers.data_types;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.hbase_index.GraphQlUtilities;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class SimpleRepresentationFetcher extends SelectorFetcher {

	public SimpleRepresentationFetcher(String type, HbaseModelAdaptor adaptor, GraphQlUtilities utilities) {
		super(type, adaptor, utilities);
	}

	@Override
	public Selector _get(DataFetchingEnvironment environment) {
		final Object simpleRepresentation = environment.getArgument("simpleRep");
		// if (simpleRepresentation != null) {
		// return adaptor.getDataTypeBySimpleRepresentation(type, simpleRepresentation.toString());
		// }
		return super._get(environment);
	}
}