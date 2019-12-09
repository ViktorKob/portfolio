package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper.unwrap;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;

import com.alibaba.csp.sentinel.annotation.SentinelResource;

import net.thomas.portfolio.service_commons.adaptors.specific.LegalAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

// @EnableCircuitBreaker
public class LegalAdaptorImpl implements PortfolioInfrastructureAware, LegalAdaptor {
	public static final String CHECK_LEGALITY_OF_INVERTED_INDEX_LOOKUP = "checkLegalityOfInvertedIndexLookup";
	public static final String CHECK_LEGALITY_OF_STATISTICS_LOOKUP = "checkLegalityOfStatisticsLookup";
	public static final String AUDIT_LOG_INVERTED_INDEX_LOOKUP = "auditLogInvertedIndexLookup";
	public static final String AUDIT_LOG_STATISTICS_LOOKUP = "auditLogStatisticsLookup";

	private final ParameterizedTypeReference<Resource<Legality>> LEGALITY_RESOURCE = new ParameterizedTypeReference<>() {
	};

	private PortfolioUrlLibrary urlLibrary;
	private HttpRestClient client;

	@Override
	public void initialize(PortfolioUrlLibrary urlLibrary, HttpRestClient client) {
		this.urlLibrary = urlLibrary;
		this.client = client;
	}

	@Override
	@SentinelResource(value = CHECK_LEGALITY_OF_INVERTED_INDEX_LOOKUP)
	public Legality checkLegalityOfInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		final String url = urlLibrary.selectors().audit().check().invertedIndex(selectorId, legalInfo);
		return unwrap(client.loadUrlAsObject(url, GET, LEGALITY_RESOURCE));
	}

	@Override
	@SentinelResource(value = CHECK_LEGALITY_OF_STATISTICS_LOOKUP)
	public Legality checkLegalityOfStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		final String url = urlLibrary.selectors().audit().check().statistics(selectorId, legalInfo);
		return unwrap(client.loadUrlAsObject(url, GET, LEGALITY_RESOURCE));
	}

	@Override
	@SentinelResource(value = AUDIT_LOG_INVERTED_INDEX_LOOKUP)
	public Boolean auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		final String url = urlLibrary.selectors().audit().log().invertedIndex(selectorId, legalInfo);
		return client.loadUrlAsObject(url, POST);
	}

	@Override
	@SentinelResource(value = AUDIT_LOG_STATISTICS_LOOKUP)
	public Boolean auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		final String url = urlLibrary.selectors().audit().log().statistics(selectorId, legalInfo);
		return client.loadUrlAsObject(url, POST);
	}
}