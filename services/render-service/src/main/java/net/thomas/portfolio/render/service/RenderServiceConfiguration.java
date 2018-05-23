package net.thomas.portfolio.render.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import net.thomas.portfolio.common.services.ServiceDependency;

@Configuration
@ConfigurationProperties("renderService")
public class RenderServiceConfiguration {

	private ServiceDependency hbaseIndexing;

	public ServiceDependency getHbaseIndexing() {
		return hbaseIndexing;
	}

	public void setHbaseIndexing(ServiceDependency hbaseIndexing) {
		this.hbaseIndexing = hbaseIndexing;
	}
}