package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static java.util.Collections.emptyList;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class SelectorSuggestionsFetcher extends ModelDataFetcher<List<DataTypeId>> {

	public SelectorSuggestionsFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public List<DataTypeId> _get(DataFetchingEnvironment environment) {
		final Object simpleRepresentation = environment.getArgument("simpleRep");
		if (simpleRepresentation != null) {
			final List<DataTypeId> selectorSuggestionIds = adaptors.getSelectorSuggestions(simpleRepresentation.toString());
			return selectorSuggestionIds;
		}
		return emptyList();
	}
}