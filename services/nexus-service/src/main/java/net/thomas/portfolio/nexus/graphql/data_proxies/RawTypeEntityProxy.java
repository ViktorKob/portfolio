package net.thomas.portfolio.nexus.graphql.data_proxies;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;

public class RawTypeEntityProxy extends RawTypeProxy<RawDataType> {

	public RawTypeEntityProxy(RawDataType contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public RawTypeEntityProxy(DataTypeProxy<?, ?> parent, RawDataType contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}

	@Override
	public DataTypeId getId() {
		return contents.getId();
	}

	@Override
	public RawDataType _getEntity() {
		return contents;
	}
}