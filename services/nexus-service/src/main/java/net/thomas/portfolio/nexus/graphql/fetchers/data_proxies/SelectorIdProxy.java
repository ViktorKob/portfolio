package net.thomas.portfolio.nexus.graphql.fetchers.data_proxies;

import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SelectorIdProxy extends SelectorProxy<DataTypeId> {

	public SelectorIdProxy(DataTypeId contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public SelectorIdProxy(DataTypeProxy<?, ?> parent, DataTypeId contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}

	@Override
	public DataTypeId getId() {
		return contents;
	}

	@Override
	public Selector _getEntity() {
		return (Selector) adaptors.getDataType(contents);
	}
}