package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.data_proxies.DataTypeEntityProxy;
import net.thomas.portfolio.nexus.graphql.data_proxies.DataTypeProxy;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

public class SubTypeFetcher extends ModelDataFetcher<DataTypeEntityProxy> {

	private final String fieldName;

	public SubTypeFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
		this.fieldName = fieldName;
	}

	@Override
	public DataTypeEntityProxy get(DataFetchingEnvironment environment) {
		final DataType entity = ((DataTypeProxy<?, ?>) environment.getSource()).getEntity();
		if (entity != null) {
			return getSubType(environment, entity);
		} else {
			return null;
		}
	}

	private DataTypeEntityProxy getSubType(DataFetchingEnvironment environment, final DataType entity) {
		final DataType subEntity = entity.get(fieldName);
		if (subEntity != null) {
			return new DataTypeEntityProxy((DataTypeProxy<?, ?>) environment.getSource(), subEntity, adaptors);
		} else {
			return null;
		}
	}
}