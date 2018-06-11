package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static java.util.Collections.emptyList;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

public class DocumentListFetcher extends ModelDataFetcher<List<DocumentInfo>> {

	private final DateConverter dateFormatter;

	public DocumentListFetcher(Adaptors adaptors) {
		super(adaptors/* , 200 */);
		dateFormatter = adaptors.getIec8601DateConverter();
	}

	@Override
	public List<DocumentInfo> _get(DataFetchingEnvironment environment) {
		DataTypeId selectorId = null;
		if (environment.getSource() instanceof Selector) {
			selectorId = ((Selector) environment.getSource()).getId();
		} else if (environment.getSource() instanceof DataTypeId) {
			selectorId = environment.getSource();
		}

		final InvertedIndexLookupRequest request = convertToSearch(selectorId, environment.getArguments());
		final DataTypeId id = request.selectorId;
		if (isIllegal(id, request)) {
			throw new GraphQLException("Search for selector " + id.type + "-" + id.uid + " must be justified by a specific user");
		} else {
			if (adaptors.auditLogInvertedIndexLookup(id, request.legalInfo)) {
				return lookupDocumentType(request);
			} else {
				return emptyList();
			}
		}
	}

	private InvertedIndexLookupRequest convertToSearch(DataTypeId selectorId, Map<String, Object> arguments) {
		final Bounds bounds = extractBounds(arguments);
		final LegalInformation legalInfo = extractLegalInformation(arguments, bounds);
		final Collection<String> documentTypes = determineDocumentTypes(selectorId.type, arguments);
		final Collection<String> relations = determineRelations(selectorId.type, arguments);
		return new InvertedIndexLookupRequest(selectorId, legalInfo, bounds, documentTypes, relations);
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

	private Collection<String> determineDocumentTypes(String selectorType, Map<String, Object> arguments) {
		@SuppressWarnings("unchecked")
		final List<String> documentTypes = (List<String>) arguments.get("documentTypes");
		if (documentTypes == null || documentTypes.isEmpty()) {
			return adaptors.getIndexedDocumentTypes(selectorType);
		} else {
			return documentTypes;
		}
	}

	private Collection<String> determineRelations(String selectorType, Map<String, Object> arguments) {
		@SuppressWarnings("unchecked")
		final List<String> relations = (List<String>) arguments.get("relations");
		if (relations == null || relations.isEmpty()) {
			return adaptors.getIndexedRelations(selectorType);
		} else {
			return relations;
		}
	}

	private List<DocumentInfo> lookupDocumentType(final InvertedIndexLookupRequest request) {
		return adaptors.lookupSelectorInInvertedIndex(request);
	}

	private boolean isIllegal(final DataTypeId id, final InvertedIndexLookupRequest request) {
		return ILLEGAL == adaptors.checkLegalityOfSelectorQuery(id, request.legalInfo);
	}
}