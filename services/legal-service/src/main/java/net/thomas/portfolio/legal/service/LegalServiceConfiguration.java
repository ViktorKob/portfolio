package net.thomas.portfolio.legal.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import net.thomas.portfolio.common.services.parameters.ServiceDependency;

@Configuration
@ConfigurationProperties("legal-service")
public class LegalServiceConfiguration {

	private ServiceDependency hbaseIndexing;
	private ServiceDependency analytics;

	public ServiceDependency getHbaseIndexing() {
		return hbaseIndexing;
	}

	public void setHbaseIndexing(ServiceDependency hbaseIndexing) {
		this.hbaseIndexing = hbaseIndexing;
	}

	public ServiceDependency getAnalytics() {
		return analytics;
	}

	public void setAnalytics(ServiceDependency analytics) {
		this.analytics = analytics;
	}
}