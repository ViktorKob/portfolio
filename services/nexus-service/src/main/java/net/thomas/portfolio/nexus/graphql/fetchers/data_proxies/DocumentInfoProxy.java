package net.thomas.portfolio.nexus.graphql.fetchers.data_proxies;

import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;

public class DocumentInfoProxy extends DocumentProxy<DocumentInfo> {

	public DocumentInfoProxy(DocumentInfo contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public DocumentInfoProxy(DataTypeProxy<?, ?> parent, DocumentInfo contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}

	@Override
	public DataTypeId getId() {
		return contents.getId();
	}

	@Override
	public Document _getEntity() {
		return (Document) adaptors.getDataType(contents.getId());
	}
}