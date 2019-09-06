package net.thomas.portfolio.service_commons.network.urls;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.services.ContextPathSection;

public class PortfolioUrlSuffixBuilder implements UrlSuffixBuilder {
	private static final Logger LOG = getLogger(PortfolioUrlSuffixBuilder.class);

	public String buildUrlSuffix(final ContextPathSection servicePath, final ContextPathSection resourcePath) {
		return buildUrlSuffix(servicePath, resourcePath, emptySet());
	}

	public String buildUrlSuffix(final ContextPathSection servicePath, final ContextPathSection resourcePath, final Parameter... parameters) {
		return buildUrlSuffix(servicePath, resourcePath, asList(parameters));
	}

	public String buildUrlSuffix(final ContextPathSection servicePath, final ContextPathSection resourcePath, final ParameterGroup... groups) {
		final Collection<Parameter> parameters = stream(groups).map(ParameterGroup::getParameters).flatMap(Arrays::stream).collect(Collectors.toList());
		return buildUrlSuffix(servicePath, resourcePath, parameters);
	}

	public String buildUrlSuffix(final ContextPathSection servicePath, final ContextPathSection resourcePath, final Collection<Parameter> parameters) {
		final String urlSuffix = buildResourceUrl(servicePath, resourcePath);
		final String parameterString = buildParameterString(parameters);
		return urlSuffix + (parameterString.length() > 0 ? "?" + parameterString : "");
	}

	private String buildResourceUrl(final ContextPathSection servicePath, final ContextPathSection resourcePath) {
		return servicePath.getContextPath() + resourcePath.getContextPath();
	}

	private String buildParameterString(final Collection<Parameter> parameters) {
		return parameters.stream().filter(Objects::nonNull).filter(parameter -> parameter.hasValue()).map(parameter -> {
			try {
				return parameter.getName() + "=" + encode(parameter.getValue().toString(), UTF_8.toString());
			} catch (final UnsupportedEncodingException e) {
				LOG.error("Unable to URL encode parameter " + parameter.getName(), e);
				throw new RuntimeException("Unable to URL encode parameter " + parameter.getName(), e);
			}
		}).collect(joining("&"));
	}
}