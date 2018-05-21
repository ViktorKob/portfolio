package net.graphql.fetchers.data_types;

import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.data.SelectorSearch;
import net.model.types.Selector;
import net.model.util.DateConverter;

public class IndexableDocumentSearchFetcher extends ModelDataFetcher<SelectorSearch> {

	private final DateConverter dateFormatter;

	public IndexableDocumentSearchFetcher(ModelAdaptor adaptor) {
		super(adaptor, 10);
		dateFormatter = adaptor.getDateConverter();
	}

	@Override
	public SelectorSearch _get(DataFetchingEnvironment environment) {
		final Selector selector = environment.getSource();
		return convertToSearch(selector, environment.getArguments());
	}

	private SelectorSearch convertToSearch(Selector selector, Map<String, Object> arguments) {
		final Integer offset = (Integer) arguments.get("offset");
		final Integer limit = (Integer) arguments.get("limit");
		final Long after = determineAfter(arguments);
		final Long before = determineBefore(arguments);
		return new SelectorSearch(selector, offset, limit, after, before);
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