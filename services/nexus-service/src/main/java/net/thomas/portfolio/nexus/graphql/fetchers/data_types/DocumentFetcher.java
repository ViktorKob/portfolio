package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

public class DocumentFetcher extends EntityFetcher<Document> {

	public DocumentFetcher(String type, Adaptors adaptors) {
		super(type, adaptors);
	}
}