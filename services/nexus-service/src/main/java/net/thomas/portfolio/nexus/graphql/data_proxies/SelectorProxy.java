package net.thomas.portfolio.nexus.graphql.data_proxies;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public abstract class SelectorProxy<CONTENTS> extends DataTypeProxy<CONTENTS, Selector> {

	public SelectorProxy(CONTENTS contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public SelectorProxy(DataTypeProxy<?, ?> parent, CONTENTS contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}
}