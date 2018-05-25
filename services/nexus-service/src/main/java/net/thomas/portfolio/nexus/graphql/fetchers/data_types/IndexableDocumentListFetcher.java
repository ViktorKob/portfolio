package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static java.util.Collections.emptyList;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.RecognitionLevel.KNOWN;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.SelectorSearch;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class IndexableDocumentListFetcher extends ModelDataFetcher<List<DocumentInfo>> {

	private final Indexable indexable;

	public IndexableDocumentListFetcher(Indexable indexable, Adaptors adaptors) {
		super(adaptors/* , 200 */);
		this.indexable = indexable;
	}

	@Override
	public List<DocumentInfo> _get(DataFetchingEnvironment environment) {
		final SelectorSearch search = environment.getSource();
		if (isDanish(search.selector) && justificationIsMissing(search.selector)) {
			return emptyList();
		} else {
			return adaptors.invertedIndexLookup(search, indexable);
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