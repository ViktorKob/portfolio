package net.thomas.portfolio.graphql.fetchers.data_types;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.SelectorSearch;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class IndexableDocumentListFetcher extends ModelDataFetcher<List<Document>> {

	private final Indexable indexable;

	public IndexableDocumentListFetcher(Indexable indexable, HbaseModelAdaptor adaptor) {
		super(adaptor, 200);
		this.indexable = indexable;
	}

	@Override
	public List<Document> _get(DataFetchingEnvironment environment) {
		final SelectorSearch search = environment.getSource();
		// if (RecognitionLevel.KNOWN == adaptor.getPreviousKnowledgeFor(search.selector).isDanish && (search.selector.getJustification() == null
		// || search.selector.getJustification().trim().isEmpty())) {
		// return Collections.<Document> emptyList();
		// } else {
		return adaptor.doSearch(search, indexable);
		// }
	}
}