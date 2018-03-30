package net.thomas.shared_objects.services;

public enum ServiceEndpoint {
	CLUSTER_DATA_SERVICE("/ClusterDataService");

	private final String path;

	private ServiceEndpoint(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
