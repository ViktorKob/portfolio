package net.thomas.portfolio.nexus.graphql.fetchers.references;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Source;

public class ReferenceSourceFetcher extends ModelDataFetcher<Source> {

	public ReferenceSourceFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Source _get(DataFetchingEnvironment environment) {
		final Reference reference = (Reference) environment.getSource();
		return reference.getSource();
	}
}