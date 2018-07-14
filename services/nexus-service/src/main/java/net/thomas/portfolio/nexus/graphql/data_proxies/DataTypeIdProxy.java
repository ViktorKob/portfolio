package net.thomas.portfolio.nexus.graphql.data_proxies;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class DataTypeIdProxy extends DataTypeProxy<DataTypeId, DataType> {

	public DataTypeIdProxy(DataTypeId contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public DataTypeIdProxy(DataTypeProxy<?, ?> parent, DataTypeId contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}

	@Override
	public DataTypeId getId() {
		return contents;
	}

	@Override
	public DataType _getEntity() {
		return adaptors.getDataType(contents);
	}
}