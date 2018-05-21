package net.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("clusterDataService")
public class ClusterDataServiceConfiguration {

	private Kerberos kerberos;

	public Kerberos getKerberos() {
		return kerberos;
	}

	public void setKerberos(Kerberos kerberos) {
		this.kerberos = kerberos;
	}

	public static class Kerberos {

		private String user;
		private String keytabFilePath;

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getKeytabFilePath() {
			return keytabFilePath;
		}

		public void setKeytabFilePath(String keytabFilePath) {
			this.keytabFilePath = keytabFilePath;
		}
	}
}