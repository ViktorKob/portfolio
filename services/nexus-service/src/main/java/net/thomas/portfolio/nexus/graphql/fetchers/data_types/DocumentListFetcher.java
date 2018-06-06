package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;

import java.util.HashSet;
import java.util.List;

import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;

public class DocumentListFetcher extends ModelDataFetcher<List<DocumentInfo>> {

	private final String documentType;

	public DocumentListFetcher(String documentType, Adaptors adaptors) {
		super(adaptors/* , 200 */);
		this.documentType = documentType;
	}

	@Override
	public List<DocumentInfo> _get(DataFetchingEnvironment environment) {
		final InvertedIndexLookupRequest request = new InvertedIndexLookupRequest(environment.getSource());
		final DataTypeId id = request.selectorId;
		if ("Document".equals(documentType)) {
			request.documentTypes = new HashSet<>(adaptors.getIndexedDocumentTypes(id.getDti_type()));
		} else {
			request.documentTypes = singleton(documentType);
		}
		if (isIllegal(id, request)) {
			throw new GraphQLException("Search for selector " + id.type + "-" + id.uid + " must be justified by a specific user");
		} else {
			if (adaptors.auditLogInvertedIndexLookup(id, request.legalInfo)) {
				return lookupDocumentType(request);
			}
			return emptyList();
		}
	}

	private List<DocumentInfo> lookupDocumentType(final InvertedIndexLookupRequest request) {
		return adaptors.lookupSelectorInInvertedIndex(request);
	}

	private boolean isIllegal(final DataTypeId id, final InvertedIndexLookupRequest request) {
		return ILLEGAL == adaptors.checkLegalityOfSelectorQuery(id, request.legalInfo);
	}
}