package net.thomas.portfolio.hbase_index.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import net.thomas.portfolio.common.services.parameters.ServiceDependency;

@Configuration
@ConfigurationProperties("hbase-indexing-service")
public class HbaseIndexingServiceConfiguration {

	private long randomSeed;
	private ServiceDependency legal;

	public long getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
	}

	public ServiceDependency getLegal() {
		return legal;
	}

	public void setLegal(ServiceDependency legal) {
		this.legal = legal;
	}
}