package net.thomas.portfolio.service_commons.network;

import net.thomas.portfolio.service_commons.network.urls.UrlFactory;

public interface PortfolioInfrastructureAware {
	void initialize(UrlFactory urlFactory, HttpRestClient client);
}