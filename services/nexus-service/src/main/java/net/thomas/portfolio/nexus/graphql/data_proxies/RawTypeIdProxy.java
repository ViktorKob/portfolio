package net.thomas.portfolio.nexus.graphql.data_proxies;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;

public class RawTypeIdProxy extends RawTypeProxy<DataTypeId> {

	public RawTypeIdProxy(DataTypeId contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public RawTypeIdProxy(DataTypeProxy<?, ?> parent, DataTypeId contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}

	@Override
	public DataTypeId getId() {
		return contents;
	}

	@Override
	public RawDataType _getEntity() {
		return (RawDataType) adaptors.getDataType(contents);
	}
}