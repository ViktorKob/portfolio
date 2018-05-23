package net.thomas.portfolio.graphql.fetchers.references;

import java.util.Set;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Classification;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class ReferenceClassificationsFetcher extends ModelDataFetcher<Set<Classification>> {

	public ReferenceClassificationsFetcher(HbaseModelAdaptor adaptor) {
		super(adaptor, 0);
	}

	@Override
	public Set<Classification> _get(DataFetchingEnvironment environment) {
		final Reference reference = (Reference) environment.getSource();
		return reference.classifications;
	}
}