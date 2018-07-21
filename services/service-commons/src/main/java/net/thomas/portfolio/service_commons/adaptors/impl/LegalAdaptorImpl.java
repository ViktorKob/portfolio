package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.enums.LegalServiceEndpoint.AUDIT_LOG;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.INVERTED_INDEX_QUERY;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.LEGAL_ROOT;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.LEGAL_RULES;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.STATISTICS_LOOKUP;
import static net.thomas.portfolio.service_commons.network.ServiceEndpointBuilder.asEndpoint;
import static net.thomas.portfolio.services.Service.LEGAL_SERVICE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.thomas.portfolio.service_commons.adaptors.specific.LegalAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.HttpRestClientInitializable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

@EnableCircuitBreaker
public class LegalAdaptorImpl implements HttpRestClientInitializable, LegalAdaptor {

	private HttpRestClient client;

	@Override
	public void initialize(HttpRestClient client) {
		this.client = client;
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Legality checkLegalityOfInvertedIndexQuery(DataTypeId selectorId, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, asEndpoint(LEGAL_ROOT, selectorId, INVERTED_INDEX_QUERY, LEGAL_RULES), GET, Legality.class, legalInfo);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Legality checkLegalityOfStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, asEndpoint(LEGAL_ROOT, selectorId, STATISTICS_LOOKUP, LEGAL_RULES), GET, Legality.class, legalInfo);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Boolean auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, asEndpoint(LEGAL_ROOT, selectorId, INVERTED_INDEX_QUERY, AUDIT_LOG), POST, Boolean.class, legalInfo);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Boolean auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, asEndpoint(LEGAL_ROOT, selectorId, STATISTICS_LOOKUP, AUDIT_LOG), POST, Boolean.class, legalInfo);
	}
}