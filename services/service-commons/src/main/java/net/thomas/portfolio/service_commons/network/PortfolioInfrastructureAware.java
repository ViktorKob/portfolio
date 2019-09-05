package net.thomas.portfolio.service_commons.network;

public interface PortfolioInfrastructureAware {
	void initialize(UrlFactory urlFactory, HttpRestClient client);
}