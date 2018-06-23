package net.thomas.portfolio.nexus.graphql.fetchers.references;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;

public class ReferenceOriginalIdFetcher extends ModelDataFetcher<String> {

	public ReferenceOriginalIdFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String get(DataFetchingEnvironment environment) {
		final Reference reference = (Reference) environment.getSource();
		return reference.getOriginalId();
	}
}