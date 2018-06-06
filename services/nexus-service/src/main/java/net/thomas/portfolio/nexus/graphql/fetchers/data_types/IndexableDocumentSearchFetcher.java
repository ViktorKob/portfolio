package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static java.util.Collections.emptySet;

import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

public class IndexableDocumentSearchFetcher extends ModelDataFetcher<InvertedIndexLookupRequest> {

	private final DateConverter dateFormatter;

	public IndexableDocumentSearchFetcher(Adaptors adaptors) {
		super(adaptors/* , 10 */);
		dateFormatter = adaptors.getIec8601DateConverter();
	}

	@Override
	public InvertedIndexLookupRequest _get(DataFetchingEnvironment environment) {
		final Selector selector = environment.getSource();
		// TODO[Thomas]: Change when Interfaces are added in GraphQL model
		return convertToSearch(selector, environment.getArguments());
	}

	private InvertedIndexLookupRequest convertToSearch(Selector selector, Map<String, Object> arguments) {
		final Bounds bounds = extractBounds(arguments);
		final LegalInformation legalInfo = extractLegalInformation(arguments, bounds);
		return new InvertedIndexLookupRequest(selector.getId(), legalInfo, bounds, emptySet(), emptySet());
	}

	private LegalInformation extractLegalInformation(Map<String, Object> arguments, Bounds bounds) {
		final String user = (String) arguments.get("user");
		final String justification = (String) arguments.get("justification");
		return new LegalInformation(user, justification, bounds.after, bounds.before);
	}

	private Bounds extractBounds(Map<String, Object> arguments) {
		final Integer offset = (Integer) arguments.get("offset");
		final Integer limit = (Integer) arguments.get("limit");
		final Long after = determineAfter(arguments);
		final Long before = determineBefore(arguments);
		return new Bounds(offset, limit, after, before);
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