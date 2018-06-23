package net.thomas.portfolio.nexus.graphql.fetchers.references;

import java.util.Set;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Classification;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;

public class ReferenceClassificationsFetcher extends ModelDataFetcher<Set<Classification>> {

	public ReferenceClassificationsFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Set<Classification> get(DataFetchingEnvironment environment) {
		final Reference reference = (Reference) environment.getSource();
		return reference.getClassifications();
	}
}