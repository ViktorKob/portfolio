package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.enums.LegalServiceEndpoint.AUDIT_LOG_INVERTED_INDEX_LOOKUP;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.AUDIT_LOG_STATISTICS_LOOKUP;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.CHECK_LEGALITY_OF_QUERY_ON_SELECTOR;
import static net.thomas.portfolio.services.Service.LEGAL_SERVICE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.thomas.portfolio.service_commons.adaptors.specific.LegalAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

@EnableCircuitBreaker
public class LegalAdaptorImpl implements LegalAdaptor {

	private HttpRestClient client;

	public void initialize(HttpRestClient client) {
		this.client = client;
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Boolean auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, AUDIT_LOG_INVERTED_INDEX_LOOKUP, POST, Boolean.class, selectorId, legalInfo);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Boolean auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, AUDIT_LOG_STATISTICS_LOOKUP, POST, Boolean.class, selectorId, legalInfo);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Legality checkLegalityOfSelectorQuery(DataTypeId selectorId, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, CHECK_LEGALITY_OF_QUERY_ON_SELECTOR, GET, Legality.class, selectorId, legalInfo);
	}
}