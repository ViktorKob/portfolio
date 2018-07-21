package net.thomas.portfolio.nexus.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import net.thomas.portfolio.common.services.parameters.ServiceDependency;

@Configuration
@ConfigurationProperties("nexus-service")
public class NexusServiceConfiguration {

	private ServiceDependency analytics;
	private ServiceDependency hbaseIndexing;
	private ServiceDependency legal;
	private ServiceDependency rendering;
	private ServiceDependency usage;
	private Kerberos kerberos;

	public ServiceDependency getAnalytics() {
		return analytics;
	}

	public void setAnalytics(ServiceDependency analytics) {
		this.analytics = analytics;
	}

	public ServiceDependency getHbaseIndexing() {
		return hbaseIndexing;
	}

	public void setHbaseIndexing(ServiceDependency hbaseIndexing) {
		this.hbaseIndexing = hbaseIndexing;
	}

	public ServiceDependency getLegal() {
		return legal;
	}

	public void setLegal(ServiceDependency legal) {
		this.legal = legal;
	}

	public ServiceDependency getRendering() {
		return rendering;
	}

	public void setRendering(ServiceDependency rendering) {
		this.rendering = rendering;
	}

	public ServiceDependency getUsage() {
		return usage;
	}

	public void setUsage(ServiceDependency usage) {
		this.usage = usage;
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