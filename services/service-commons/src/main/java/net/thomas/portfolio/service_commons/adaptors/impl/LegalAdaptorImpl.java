package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper.unwrap;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.thomas.portfolio.service_commons.adaptors.specific.LegalAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

@EnableCircuitBreaker
public class LegalAdaptorImpl implements PortfolioInfrastructureAware, LegalAdaptor {
	private final ParameterizedTypeReference<Resource<Legality>> LEGALITY_RESOURCE = new ParameterizedTypeReference<Resource<Legality>>() {
	};

	private PortfolioUrlLibrary urlLibrary;
	private HttpRestClient client;

	@Override
	public void initialize(final UrlFactory urlFactory, final HttpRestClient client) {
		urlLibrary = new PortfolioUrlLibrary(urlFactory);
		this.client = client;
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Legality checkLegalityOfInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		final String url = urlLibrary.legal.audit.check.invertedIndex(selectorId, legalInfo);
		return unwrap(client.loadUrlAsObject(url, GET, LEGALITY_RESOURCE));
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Legality checkLegalityOfStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		final String url = urlLibrary.legal.audit.check.statistics(selectorId, legalInfo);
		return unwrap(client.loadUrlAsObject(url, GET, LEGALITY_RESOURCE));
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Boolean auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		final String url = urlLibrary.legal.audit.log.invertedIndex(selectorId, legalInfo);
		return client.loadUrlAsObject(url, POST, Boolean.class);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Boolean auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		final String url = urlLibrary.legal.audit.log.statistics(selectorId, legalInfo);
		return client.loadUrlAsObject(url, POST, Boolean.class);
	}
}