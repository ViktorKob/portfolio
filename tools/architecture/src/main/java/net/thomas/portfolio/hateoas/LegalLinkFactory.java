package net.thomas.portfolio.hateoas;

import static net.thomas.portfolio.globals.LegalServiceGlobals.HISTORY_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_ROOT_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.LEGAL_SERVICE_PATH;

public class LegalLinkFactory extends PortfolioLinkFactory {
	public LegalLinkFactory(String globalUrlPrefix) {
		super(globalUrlPrefix);
	}

	public String getRootLink() {
		return globalUrlPrefix + LEGAL_SERVICE_PATH + LEGAL_ROOT_PATH;
	}

	public String getHistoryLink() {
		return getRootLink() + HISTORY_PATH;
	}

	public String getHistoryItemLink(int itemId) {
		return getHistoryLink() + slash(itemId);
	}
}
