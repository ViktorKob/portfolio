package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.enums.LegalServiceEndpoint.AUDIT_LOG_INVERTED_INDEX_LOOKUP;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.AUDIT_LOG_STATISTICS_LOOKUP;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.LEGALITY_OF_INVERTED_INDEX_QUERY;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.LEGALITY_OF_STATISTICS_LOOKUP;
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
		return client.loadUrlAsObject(LEGAL_SERVICE, () -> {
			return LEGALITY_OF_INVERTED_INDEX_QUERY.getPath() + "/" + selectorId.type + "/" + selectorId.uid;
		}, GET, Legality.class, legalInfo);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Legality checkLegalityOfStatisticsLookup(DataTypeId id, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, () -> {
			return LEGALITY_OF_STATISTICS_LOOKUP.getPath() + "/" + id.type + "/" + id.uid;
		}, GET, Legality.class, legalInfo);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Boolean auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, () -> {
			return AUDIT_LOG_INVERTED_INDEX_LOOKUP.getPath() + "/" + selectorId.type + "/" + selectorId.uid;
		}, POST, Boolean.class, legalInfo);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Boolean auditLogStatisticsLookup(DataTypeId id, LegalInformation legalInfo) {
		return client.loadUrlAsObject(LEGAL_SERVICE, () -> {
			return AUDIT_LOG_STATISTICS_LOOKUP.getPath() + "/" + id.type + "/" + id.uid;
		}, POST, Boolean.class, legalInfo);
	}
}