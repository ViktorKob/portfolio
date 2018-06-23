package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static java.util.stream.Collectors.toList;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.data_proxies.DataTypeEntityProxy;
import net.thomas.portfolio.nexus.graphql.data_proxies.DataTypeProxy;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class SubTypeArrayFetcher extends ModelDataFetcher<List<DataTypeEntityProxy>> {

	private final String fieldName;

	public SubTypeArrayFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
		this.fieldName = fieldName;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<DataTypeEntityProxy> get(DataFetchingEnvironment environment) {
		final DataTypeProxy<?, ?> parentProxy = (DataTypeProxy<?, ?>) environment.getSource();
		return convert(parentProxy, (List<? extends DataType>) parentProxy.getEntity()
			.get(fieldName));
	}

	private List<DataTypeEntityProxy> convert(DataTypeProxy<?, ?> parentProxy, List<? extends DataType> entities) {
		return entities.stream()
			.map(entity -> new DataTypeEntityProxy(parentProxy, entity, adaptors))
			.collect(toList());
	}
}