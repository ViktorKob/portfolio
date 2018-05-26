package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;

public class IndexableDocumentSearchFetcher extends ModelDataFetcher<InvertedIndexLookupRequest> {

	private final DateConverter dateFormatter;

	public IndexableDocumentSearchFetcher(Adaptors adaptors) {
		super(adaptors/* , 10 */);
		dateFormatter = adaptors.getDateConverter();
	}

	@Override
	public InvertedIndexLookupRequest _get(DataFetchingEnvironment environment) {
		final Selector selector = environment.getSource();
		return convertToSearch(selector, environment.getArguments());
	}

	private InvertedIndexLookupRequest convertToSearch(Selector selector, Map<String, Object> arguments) {
		final Integer offset = (Integer) arguments.get("offset");
		final Integer limit = (Integer) arguments.get("limit");
		final Long after = determineAfter(arguments);
		final Long before = determineBefore(arguments);
		return new InvertedIndexLookupRequest(selector.getId(), offset, limit, after, before);
	}

	private Long determineAfter(Map<String, Object> arguments) {
		Long after = (Long) arguments.get("after");
		if (after == null && arguments.get("afterDate") != null) {
			after = dateFormatter.parseTimestamp((String) arguments.get("afterDate"));
		}
		return after;
	}

	private Long determineBefore(Map<String, Object> arguments) {
		Long before = (Long) arguments.get("before");
		if (before == null && arguments.get("beforeDate") != null) {
			before = dateFormatter.parseTimestamp((String) arguments.get("beforeDate"));
		}
		return before;
	}
}