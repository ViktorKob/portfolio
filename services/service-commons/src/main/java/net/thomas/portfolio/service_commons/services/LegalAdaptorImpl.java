package net.thomas.portfolio.service_commons.services;

import static net.thomas.portfolio.enums.LegalServiceEndpoint.AUDIT_LOG_INVERTED_INDEX_LOOKUP;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.AUDIT_LOG_STATISTICS_LOOKUP;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.CHECK_LEGALITY_OF_QUERY_ON_SELECTOR;
import static net.thomas.portfolio.services.Service.LEGAL_SERVICE;

import net.thomas.portfolio.shared_objects.adaptors.LegalAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

public class LegalAdaptorImpl implements LegalAdaptor {

	private final HttpRestClient client;

	public LegalAdaptorImpl(HttpRestClient client) {
		this.client = client;
	}

	@Override
	public Boolean auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, AUDIT_LOG_INVERTED_INDEX_LOOKUP, Boolean.class, selectorId, legalInfo);
	}

	@Override
	public Boolean auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, AUDIT_LOG_STATISTICS_LOOKUP, Boolean.class, selectorId, legalInfo);
	}

	@Override
	public Legality checkLegalityOfSelectorQuery(DataTypeId selectorId, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, CHECK_LEGALITY_OF_QUERY_ON_SELECTOR, Legality.class, selectorId, legalInfo);
	}
}