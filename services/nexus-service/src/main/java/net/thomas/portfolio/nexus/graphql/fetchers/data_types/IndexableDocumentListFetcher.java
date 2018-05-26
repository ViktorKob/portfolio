package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static java.util.Collections.emptyList;
import static net.thomas.portfolio.shared_objects.analytics.RecognitionLevel.KNOWN;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

public class IndexableDocumentListFetcher extends ModelDataFetcher<List<DocumentInfo>> {

	private final Indexable indexable;

	public IndexableDocumentListFetcher(Indexable indexable, Adaptors adaptors) {
		super(adaptors/* , 200 */);
		this.indexable = indexable;
	}

	@Override
	public List<DocumentInfo> _get(DataFetchingEnvironment environment) {
		final InvertedIndexLookupRequest request = environment.getSource();
		if (isDanish(request.selectorId) && justificationIsMissing(request.legalInfo)) {
			return emptyList();
		} else {
			return adaptors.invertedIndexLookup(request);
		}
	}

	private boolean isDanish(DataTypeId id) {
		return KNOWN == adaptors.getPriorKnowledge(id).isDanish;
	}

	private boolean justificationIsMissing(final LegalInformation legalInfo) {
		return legalInfo.justification == null || legalInfo.justification.isEmpty();
	}
}