package net.thomas.portfolio.nexus.graphql.data_proxies;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

public abstract class DocumentProxy<CONTENT> extends DataTypeProxy<CONTENT, Document> {

	public DocumentProxy(CONTENT contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public DocumentProxy(DataTypeProxy<?, ?> parent, CONTENT contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}

	public Long getTimeOfEvent() {
		return getEntity() == null ? null : getEntity().getTimeOfEvent();
	}

	public Long getTimeOfInterception() {
		return getEntity() == null ? null : getEntity().getTimeOfInterception();
	}
}