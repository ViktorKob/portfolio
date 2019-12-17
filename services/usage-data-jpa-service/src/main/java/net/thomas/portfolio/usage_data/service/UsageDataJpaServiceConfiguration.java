package net.thomas.portfolio.usage_data.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import net.thomas.portfolio.common.services.parameters.ServiceDependency;

@Configuration
@ConfigurationProperties("usage-data-service")
public class UsageDataJpaServiceConfiguration {

	private Database database;
	private ServiceDependency hbaseIndexing;

	public ServiceDependency getHbaseIndexing() {
		return hbaseIndexing;
	}

	public Database getDatabase() {
		return database;
	}

	public static class Database {
		private String databaseName;

		public String getDatabaseName() {
			return databaseName;
		}

		public void setDatabaseName(String databaseName) {
			this.databaseName = databaseName;
		}

		public String getConnectionString() {
			return "jdbc:sqlite:database/" + databaseName + ".db";
		}
	}

	public void setHbaseIndexing(final ServiceDependency hbaseIndexing) {
		this.hbaseIndexing = hbaseIndexing;
	}

	public void setDatabase(final Database database) {
		this.database = database;
	}
}