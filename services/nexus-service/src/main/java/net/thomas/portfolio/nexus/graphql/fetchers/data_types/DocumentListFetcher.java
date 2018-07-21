package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.AFTER;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.AFTER_DATE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.BEFORE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.BEFORE_DATE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.DOCUMENT_TYPES;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.LIMIT;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.OFFSET;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.RELATIONS;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.extractFirstThatIsPresent;
import static net.thomas.portfolio.nexus.graphql.fetchers.GlobalServiceArgumentId.USER_ID;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalServiceArgumentId.JUSTIFICATION;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument;
import net.thomas.portfolio.nexus.graphql.data_proxies.DataTypeProxy;
import net.thomas.portfolio.nexus.graphql.data_proxies.DocumentInfoProxy;
import net.thomas.portfolio.nexus.graphql.data_proxies.DocumentProxy;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

public class DocumentListFetcher extends ModelDataFetcher<List<DocumentProxy<?>>> {

	public DocumentListFetcher(Adaptors adaptors) {
		super(adaptors);
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
				return lookupDocumentType(proxy, request);
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
		final String value = getFromEnvironmentOrProxy(environment, GraphQlArgument.USER, USER_ID);
		final String justification = getFromEnvironmentOrProxy(environment, GraphQlArgument.JUSTIFICATION, JUSTIFICATION);
		return new LegalInformation(value, justification, bounds.after, bounds.before);
	}

	private Bounds extractBounds(DataFetchingEnvironment environment) {
		final Integer offset = OFFSET.extractFrom(environment);
		final Integer limit = LIMIT.extractFrom(environment);
		final Long after = extractFirstThatIsPresent(environment, MIN_VALUE, AFTER, AFTER_DATE);
		final Long before = extractFirstThatIsPresent(environment, MAX_VALUE, BEFORE, BEFORE_DATE);
		return new Bounds(offset, limit, after, before);
	}

	private Set<String> determineDocumentTypes(String selectorType, DataFetchingEnvironment environment) {
		final List<String> documentTypes = DOCUMENT_TYPES.extractFrom(environment);
		if (documentTypes == null || documentTypes.isEmpty()) {
			return adaptors.getIndexedDocumentTypes(selectorType);
		} else {
			return new HashSet<>(documentTypes);
		}
	}

	private Set<String> determineRelations(String selectorType, DataFetchingEnvironment environment) {
		final List<String> relations = RELATIONS.extractFrom(environment);
		if (relations == null || relations.isEmpty()) {
			return adaptors.getIndexedRelations(selectorType);
		} else {
			return new HashSet<>(relations);
		}
	}

	private boolean isIllegal(final DataTypeId id, final InvertedIndexLookupRequest request) {
		return ILLEGAL == adaptors.checkLegalityOfSelectorQuery(id, request.legalInfo);
	}

	private List<DocumentProxy<?>> lookupDocumentType(DataTypeProxy<?, ?> parent, final InvertedIndexLookupRequest request) {
		return convert(parent, adaptors.lookupSelectorInInvertedIndex(request));
	}

	private List<DocumentProxy<?>> convert(DataTypeProxy<?, ?> parent, DocumentInfos infos) {
		return infos.getInfos()
			.stream()
			.map(documentInfo -> new DocumentInfoProxy(parent, documentInfo, adaptors))
			.collect(toList());
	}
}