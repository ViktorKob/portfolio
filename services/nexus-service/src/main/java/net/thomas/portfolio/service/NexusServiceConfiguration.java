package net.thomas.portfolio.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import net.thomas.portfolio.common.services.ServiceDependency;

@Configuration
@ConfigurationProperties("nexusService")
public class NexusServiceConfiguration {

	private ServiceDependency hbaseIndexing;
	private ServiceDependency rendering;
	private Kerberos kerberos;

	public ServiceDependency getHbaseIndexing() {
		return hbaseIndexing;
	}

	public void setHbaseIndexing(ServiceDependency hbaseIndexing) {
		this.hbaseIndexing = hbaseIndexing;
	}

	public ServiceDependency getRendering() {
		return rendering;
	}

	public void setRendering(ServiceDependency rendering) {
		this.rendering = rendering;
	}

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