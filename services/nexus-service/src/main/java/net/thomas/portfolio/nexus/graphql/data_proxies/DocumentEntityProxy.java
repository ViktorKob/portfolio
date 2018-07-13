package net.thomas.portfolio.nexus.graphql.data_proxies;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

public class DocumentEntityProxy extends DocumentProxy<Document> {

	public DocumentEntityProxy(Document contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public DocumentEntityProxy(DataTypeProxy<?, ?> parent, Document contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}

	@Override
	public DataTypeId getId() {
		return contents.getId();
	}

	@Override
	public Document _getEntity() {
		return contents;
	}
}