package net.thomas.portfolio.service_commons.network;

import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;

public interface PortfolioInfrastructureAware {
	void initialize(PortfolioUrlLibrary urlLibrary, HttpRestClient client);
}