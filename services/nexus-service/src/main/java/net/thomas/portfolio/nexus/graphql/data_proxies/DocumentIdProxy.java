package net.thomas.portfolio.nexus.graphql.data_proxies;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

public class DocumentIdProxy extends DocumentProxy<DataTypeId> {

	public DocumentIdProxy(DataTypeId contents, Adaptors adaptors) {
		super(contents, adaptors);
	}

	public DocumentIdProxy(DataTypeProxy<?, ?> parent, DataTypeId contents, Adaptors adaptors) {
		super(parent, contents, adaptors);
	}

	@Override
	public DataTypeId getId() {
		return contents;
	}

	@Override
	public Document _getEntity() {
		return (Document) adaptors.getDataType(contents);
	}
}