package net.graphql.fetchers.data_types;

import net.graphql.fetchers.ModelAdaptor;
import net.model.types.Document;

public class DocumentFetcher extends EntityFetcher<Document> {

	public DocumentFetcher(String type, ModelAdaptor adaptor) {
		super(type, adaptor);
	}
}