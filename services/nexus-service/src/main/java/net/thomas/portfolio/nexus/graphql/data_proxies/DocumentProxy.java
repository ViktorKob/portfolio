package net.thomas.portfolio.nexus.graphql.data_proxies;

import static net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp.UNKNOWN;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public abstract class DocumentProxy<CONTENT> extends DataTypeProxy<CONTENT, Document> {

	public DocumentProxy(CONTENT contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public DocumentProxy(DataTypeProxy<?, ?> parent, CONTENT contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}

	public Timestamp getTimeOfEvent() {
		return getEntity() == null ? UNKNOWN : getEntity().getTimeOfEvent();
	}

	public Timestamp getTimeOfInterception() {
		return getEntity() == null ? UNKNOWN : getEntity().getTimeOfInterception();
	}
}