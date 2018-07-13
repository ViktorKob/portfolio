package net.thomas.portfolio.nexus.graphql.data_proxies;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;

public abstract class RawTypeProxy<CONTENT_TYPE> extends DataTypeProxy<CONTENT_TYPE, RawDataType> {

	public RawTypeProxy(CONTENT_TYPE contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public RawTypeProxy(DataTypeProxy<?, ?> parent, CONTENT_TYPE contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}
}