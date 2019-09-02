package net.thomas.portfolio.hateoas;

public class PortfolioUrlFactory {
	protected final String globalUrlPrefix;

	public PortfolioUrlFactory(String globalUrlPrefix) {
		this.globalUrlPrefix = globalUrlPrefix;
	}

	protected String slash(Object value) {
		return "/" + value;
	}

	@FunctionalInterface
	public static interface UrlGenerator {
		String generate();
	}
}