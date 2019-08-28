package net.thomas.portfolio.hateoas;

public class PortfolioLinkFactory {
	protected final String globalUrlPrefix;

	public PortfolioLinkFactory(String globalUrlPrefix) {
		this.globalUrlPrefix = globalUrlPrefix;
	}

	protected String slash(Object value) {
		return "/" + value;
	}
}