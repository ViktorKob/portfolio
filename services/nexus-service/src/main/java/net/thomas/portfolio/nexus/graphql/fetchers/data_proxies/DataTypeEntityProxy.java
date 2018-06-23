package net.thomas.portfolio.nexus.graphql.fetchers.data_proxies;

import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class DataTypeEntityProxy extends DataTypeProxy<DataType, DataType> {

	public DataTypeEntityProxy(DataType contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public DataTypeEntityProxy(DataTypeProxy<?, ?> parent, DataType contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}

	@Override
	public DataTypeId getId() {
		return contents.getId();
	}

	@Override
	public DataType _getEntity() {
		return contents;
	}
}