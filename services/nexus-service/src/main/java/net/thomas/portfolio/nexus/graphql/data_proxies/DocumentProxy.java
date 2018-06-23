package net.thomas.portfolio.nexus.graphql.data_proxies;

import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

public abstract class DocumentProxy<CONTENT> extends DataTypeProxy<CONTENT, Document> {

	public DocumentProxy(CONTENT contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public DocumentProxy(DataTypeProxy<?, ?> parent, CONTENT contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}

	public long getTimeOfEvent() {
		return getEntity().getTimeOfEvent();
	}

	public long getTimeOfInterception() {
		return getEntity().getTimeOfInterception();
	}
}