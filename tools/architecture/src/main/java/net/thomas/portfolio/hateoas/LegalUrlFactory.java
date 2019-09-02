package net.thomas.portfolio.hateoas;

import static net.thomas.portfolio.globals.LegalServiceGlobals.HISTORY_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_ROOT_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.LEGAL_SERVICE_PATH;

public class LegalUrlFactory extends PortfolioUrlFactory {
	public LegalUrlFactory(String globalUrlPrefix) {
		super(globalUrlPrefix);
	}

	public String getRootUrl() {
		return globalUrlPrefix + LEGAL_SERVICE_PATH + LEGAL_ROOT_PATH;
	}

	public String getHistoryUrl() {
		return getRootUrl() + HISTORY_PATH;
	}

	public String getHistoryItemUrl(int itemId) {
		return getHistoryUrl() + slash(itemId);
	}
}
