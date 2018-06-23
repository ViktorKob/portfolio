package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_proxies.DataTypeEntityProxy;
import net.thomas.portfolio.nexus.graphql.fetchers.data_proxies.DataTypeProxy;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class SubTypeFetcher extends ModelDataFetcher<DataTypeEntityProxy> {

	private final String fieldName;

	public SubTypeFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
		this.fieldName = fieldName;
	}

	@Override
	public DataTypeEntityProxy get(DataFetchingEnvironment environment) {
		final DataTypeProxy<?, ?> parentProxy = (DataTypeProxy<?, ?>) environment.getSource();
		final DataType subEntity = (DataType) parentProxy.getEntity()
			.get(fieldName);
		if (subEntity != null) {
			return new DataTypeEntityProxy(parentProxy, subEntity, adaptors);
		} else {
			return null;
		}
	}
}