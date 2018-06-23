package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.thomas.portfolio.nexus.graphql.fetchers.GlobalArgumentId.USER_ID;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalArgumentId.JUSTIFICATION;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalArgumentId.LOWER_BOUND_DATE;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalArgumentId.UPPER_BOUND_DATE;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.data_proxies.DataTypeProxy;
import net.thomas.portfolio.nexus.graphql.data_proxies.DocumentInfoProxy;
import net.thomas.portfolio.nexus.graphql.data_proxies.DocumentProxy;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

public class DocumentListFetcher extends ModelDataFetcher<List<DocumentProxy<?>>> {

	private final DateConverter dateFormatter;

	public DocumentListFetcher(Adaptors adaptors) {
		super(adaptors);
		dateFormatter = adaptors.getIec8601DateConverter();
	}

	@Override
	public List<DocumentProxy<?>> get(DataFetchingEnvironment environment) {
		final DataTypeProxy<?, ?> proxy = getProxy(environment);
		final InvertedIndexLookupRequest request = convertToSearch(environment);
		final DataTypeId selectorId = proxy.getId();
		if (isIllegal(selectorId, request)) {
			throw new GraphQLException("Search for selector " + selectorId.type + "-" + selectorId.uid + " must be justified by a specific user");
		} else {
			if (adaptors.auditLogInvertedIndexLookup(selectorId, request.legalInfo)) {
				return lookupDocumentType(request);
			} else {
				return emptyList();
			}
		}
	}

	private InvertedIndexLookupRequest convertToSearch(DataFetchingEnvironment environment) {
		final Bounds bounds = extractBounds(environment);
		final LegalInformation legalInfo = extractLegalInformation(environment, bounds);
		final DataTypeId id = getId(environment);
		final Set<String> documentTypes = determineDocumentTypes(id.type, environment);
		final Set<String> relations = determineRelations(id.type, environment);
		return new InvertedIndexLookupRequest(id, legalInfo, bounds, documentTypes, relations);
	}

	private LegalInformation extractLegalInformation(DataFetchingEnvironment environment, Bounds bounds) {
		final String value = getFromEnvironmentOrProxy(environment, "user", USER_ID);
		final String justification = getFromEnvironmentOrProxy(environment, "justification", JUSTIFICATION);
		return new LegalInformation(value, justification, bounds.after, bounds.before);
	}

	private Bounds extractBounds(DataFetchingEnvironment environment) {
		final Integer offset = environment.getArgument("offset");
		final Integer limit = environment.getArgument("limit");
		final Long after = determineAfter(environment);
		final Long before = determineBefore(environment);
		return new Bounds(offset, limit, after, before);
	}

	private Long determineAfter(DataFetchingEnvironment environment) {
		Long after = getFromEnvironmentOrProxy(environment, "after", LOWER_BOUND_DATE);
		if (after == null && environment.getArgument("afterDate") != null) {
			after = dateFormatter.parseTimestamp(environment.getArgument("afterDate"));
		}
		return after;
	}

	private Long determineBefore(DataFetchingEnvironment environment) {
		Long before = getFromEnvironmentOrProxy(environment, "before", UPPER_BOUND_DATE);
		if (before == null && environment.getArgument("beforeDate") != null) {
			before = dateFormatter.parseTimestamp(environment.getArgument("beforeDate"));
		}
		return before;
	}

	private Set<String> determineDocumentTypes(String selectorType, DataFetchingEnvironment environment) {
		final List<String> documentTypes = environment.getArgument("documentTypes");
		if (documentTypes == null || documentTypes.isEmpty()) {
			return adaptors.getIndexedDocumentTypes(selectorType);
		} else {
			return new HashSet<>(documentTypes);
		}
	}

	private Set<String> determineRelations(String selectorType, DataFetchingEnvironment environment) {
		final List<String> relations = environment.getArgument("relations");
		if (relations == null || relations.isEmpty()) {
			return adaptors.getIndexedRelations(selectorType);
		} else {
			return new HashSet<>(relations);
		}
	}

	private boolean isIllegal(final DataTypeId id, final InvertedIndexLookupRequest request) {
		return ILLEGAL == adaptors.checkLegalityOfSelectorQuery(id, request.legalInfo);
	}

	private List<DocumentProxy<?>> lookupDocumentType(final InvertedIndexLookupRequest request) {
		return convert(adaptors.lookupSelectorInInvertedIndex(request));
	}

	private List<DocumentProxy<?>> convert(List<DocumentInfo> lookupResult) {
		return lookupResult.stream()
			.map(documentInfo -> new DocumentInfoProxy(documentInfo, adaptors))
			.collect(toList());
	}
}