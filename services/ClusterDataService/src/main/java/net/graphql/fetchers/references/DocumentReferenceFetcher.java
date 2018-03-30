package net.graphql.fetchers.references;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.meta_data.Reference;
import net.model.types.Document;

public class DocumentReferenceFetcher extends ModelDataFetcher<List<Reference>> {

	public DocumentReferenceFetcher(ModelAdaptor adaptor) {
		super(adaptor, 50);
	}

	@Override
	public List<Reference> _get(DataFetchingEnvironment environment) {
		final Document document = (Document) environment.getSource();
		return adaptor.getReferencesFor(document);
	}
}