package net.thomas.portfolio.graphql.fetchers.references;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class ReferenceOriginalIdFetcher extends ModelDataFetcher<String> {

	public ReferenceOriginalIdFetcher(HbaseModelAdaptor adaptor) {
		super(adaptor, 0);
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final Reference reference = (Reference) environment.getSource();
		return reference.originalId;
	}
}