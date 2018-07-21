package net.thomas.portfolio.common.services.parameters;

public class ServiceDependency {
	private String name;
	private Credentials credentials;

	public ServiceDependency() {
	}

	public ServiceDependency(String name, Credentials credentials) {
		this.name = name;
		this.credentials = credentials;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}
}