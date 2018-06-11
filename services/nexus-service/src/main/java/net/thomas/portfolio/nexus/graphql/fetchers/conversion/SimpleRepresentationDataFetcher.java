package net.thomas.portfolio.nexus.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SimpleRepresentationDataFetcher extends ModelDataFetcher<Object> {

	public SimpleRepresentationDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		if (environment.getSource() == null) {
			return null;
		}
		DataTypeId selectorId = null;
		if (environment.getSource() instanceof Selector) {
			selectorId = ((Selector) environment.getSource()).getId();
		} else if (environment.getSource() instanceof DataTypeId) {
			selectorId = environment.getSource();
		}
		return adaptors.renderAsSimpleRepresentation(selectorId);
	}
}