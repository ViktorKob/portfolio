package net.thomas.portfolio.service_commons.network;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.services.ContextPathSection;
import net.thomas.portfolio.services.Service;

public class UrlFactory {
	private final UrlPrefixBuilder prefixBuilder;
	private final PortfolioUrlSuffixBuilder suffixBuilder;

	public UrlFactory(UrlPrefixBuilder prefixBuilder, PortfolioUrlSuffixBuilder suffixBuilder) {
		this.prefixBuilder = prefixBuilder;
		this.suffixBuilder = suffixBuilder;
	}

	public String buildUrl(Service service, ContextPathSection resourcePath) {
		return prefixBuilder.build() + suffixBuilder.buildUrlSuffix(service, resourcePath);
	}

	public String buildUrl(Service service, ContextPathSection resourcePath, final Parameter... parameters) {
		return prefixBuilder.build() + suffixBuilder.buildUrlSuffix(service, resourcePath, parameters);
	}

	public String buildUrl(Service service, ContextPathSection resourcePath, final ParameterGroup... groups) {
		return prefixBuilder.build() + suffixBuilder.buildUrlSuffix(service, resourcePath, groups);
	}
}