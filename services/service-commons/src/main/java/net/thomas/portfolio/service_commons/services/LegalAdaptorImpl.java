package net.thomas.portfolio.service_commons.services;

import static net.thomas.portfolio.enums.LegalServiceEndpoint.CHECK_LEGALITY_OF_INVERTED_INDEX_LOOKUP;

import net.thomas.portfolio.services.Service;
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
	public Legality checkLegalityOfInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return client.loadUrlAsObject(Service.LEGAL_SERVICE, CHECK_LEGALITY_OF_INVERTED_INDEX_LOOKUP, Legality.class, selectorId, legalInfo);
	}
}