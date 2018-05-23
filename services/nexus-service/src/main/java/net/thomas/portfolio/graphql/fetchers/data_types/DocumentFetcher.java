package net.thomas.portfolio.graphql.fetchers.data_types;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class DocumentFetcher extends EntityFetcher<Document> {

	public DocumentFetcher(String type, HbaseModelAdaptor adaptor) {
		super(type, adaptor);
	}
}