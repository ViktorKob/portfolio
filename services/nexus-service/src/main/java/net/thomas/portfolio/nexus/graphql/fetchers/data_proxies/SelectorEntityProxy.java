package net.thomas.portfolio.nexus.graphql.fetchers.data_proxies;

import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SelectorEntityProxy extends SelectorProxy<Selector> {

	public SelectorEntityProxy(Selector contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public SelectorEntityProxy(DataTypeProxy<?, ?> parent, Selector contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}

	@Override
	public DataTypeId getId() {
		return contents.getId();
	}

	@Override
	public Selector _getEntity() {
		return contents;
	}
}