package net.thomas.portfolio.hbase_index.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("hbase-indexing-service")
public class HbaseIndexingServiceConfiguration {

	private long randomSeed;
	private int populationCount;
	private int averageRelationCount;
	private int averageCommunicationCount;
	private String storageRootPath;

	public long getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
	}

	public int getPopulationCount() {
		return populationCount;
	}

	public void setPopulationCount(int populationCount) {
		this.populationCount = populationCount;
	}

	public int getAverageRelationCount() {
		return averageRelationCount;
	}

	public void setAverageRelationCount(int averageRelationCount) {
		this.averageRelationCount = averageRelationCount;
	}

	public int getAverageCommunicationCount() {
		return averageCommunicationCount;
	}

	public void setAverageCommunicationCount(int averageCommunicationCount) {
		this.averageCommunicationCount = averageCommunicationCount;
	}

	public String getStorageRootPath() {
		return storageRootPath;
	}

	public void setStorageRootPath(String storageRootPath) {
		this.storageRootPath = storageRootPath;
	}
}