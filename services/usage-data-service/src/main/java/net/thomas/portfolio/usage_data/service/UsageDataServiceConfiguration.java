package net.thomas.portfolio.usage_data.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import net.thomas.portfolio.common.services.parameters.ServiceDependency;

@Configuration
@ConfigurationProperties("usage-data-service")
public class UsageDataServiceConfiguration {

	private Database database;
	private ServiceDependency hbaseIndexing;

	public ServiceDependency getHbaseIndexing() {
		return hbaseIndexing;
	}

	public Database getDatabase() {
		return database;
	}

	public static class Database {
		private String host;
		private int port;
		private String schema;
		private String user;
		private String password;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getSchema() {
			return schema;
		}

		public void setSchema(String schema) {
			this.schema = schema;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getConnectionString(boolean withSchema) {
			return "jdbc:mysql://" + host + ":" + port + (withSchema ? "/" + schema : "");
		}
	}

	public void setHbaseIndexing(ServiceDependency hbaseIndexing) {
		this.hbaseIndexing = hbaseIndexing;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}
}