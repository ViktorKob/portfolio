package net.graphql.fetchers.data_types;

import java.util.Collections;
import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.data.SelectorSearch;
import net.model.meta_data.Indexable;
import net.model.meta_data.RecognitionLevel;
import net.model.types.Document;

public class IndexableDocumentListFetcher extends ModelDataFetcher<List<Document>> {

	private final Indexable indexable;

	public IndexableDocumentListFetcher(Indexable indexable, ModelAdaptor adaptor) {
		super(adaptor, 200);
		this.indexable = indexable;
	}

	@Override
	public List<Document> _get(DataFetchingEnvironment environment) {
		final SelectorSearch search = environment.getSource();
		if (RecognitionLevel.KNOWN == adaptor.getPreviousKnowledgeFor(search.selector).isDanish && (search.selector.getJustification() == null
				|| search.selector.getJustification().trim().isEmpty())) {
			return Collections.<Document> emptyList();
		} else {
			return adaptor.doSearch(search, indexable);
		}
	}
}