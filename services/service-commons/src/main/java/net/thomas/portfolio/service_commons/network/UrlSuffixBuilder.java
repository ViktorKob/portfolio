package net.thomas.portfolio.service_commons.network;

import java.util.Collection;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.services.ContextPathSection;

public interface UrlSuffixBuilder {
	public String buildUrlSuffix(final ContextPathSection servicePath, final ContextPathSection resourcePath);

	public String buildUrlSuffix(final ContextPathSection servicePath, final ContextPathSection resourcePath, final Parameter... parameters);

	public String buildUrlSuffix(final ContextPathSection servicePath, final ContextPathSection resourcePath, final ParameterGroup... groups);

	public String buildUrlSuffix(final ContextPathSection servicePath, final ContextPathSection resourcePath, final Collection<Parameter> parameters);
}