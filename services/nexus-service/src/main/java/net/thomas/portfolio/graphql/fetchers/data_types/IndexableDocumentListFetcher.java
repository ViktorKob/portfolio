package net.thomas.portfolio.graphql.fetchers.data_types;

import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.RecognitionLevel.KNOWN;

import java.util.Collections;
import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.SelectorSearch;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public class IndexableDocumentListFetcher extends ModelDataFetcher<List<Document>> {

	private final Indexable indexable;

	public IndexableDocumentListFetcher(Indexable indexable, Adaptors adaptors) {
		super(adaptors/* , 200 */);
		this.indexable = indexable;
	}

	@Override
	public List<Document> _get(DataFetchingEnvironment environment) {
		final SelectorSearch search = environment.getSource();
		if (isDanish(search.selector) && justificationIsMissing(search.selector)) {
			return Collections.<Document>emptyList();
		} else {
			return adaptors.doSearch(search, indexable);
		}
	}

	private boolean isDanish(final Selector selector) {
		return KNOWN == adaptors.getPreviousKnowledgeFor(selector).isDanish;
	}

	private boolean justificationIsMissing(final Selector selector) {
		return selector.get("justification") == null || selector.get("justification")
			.toString()
			.trim()
			.isEmpty();
	}
}