package net.thomas.portfolio.hbase_index.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("hbase-indexing-service")
public class HbaseIndexingServiceConfiguration {

	private long randomSeed;

	public long getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
	}
}