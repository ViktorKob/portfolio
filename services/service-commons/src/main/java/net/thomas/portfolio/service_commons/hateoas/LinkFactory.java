package net.thomas.portfolio.service_commons.hateoas;

import org.springframework.hateoas.Link;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.service_commons.network.UrlFactory;
import net.thomas.portfolio.services.ContextPathSection;
import net.thomas.portfolio.services.Service;

public class LinkFactory {
	private final UrlFactory urlFactory;

	public LinkFactory(UrlFactory urlFactory) {
		this.urlFactory = urlFactory;
	}

	public Link buildUrl(String relation, Service service, ContextPathSection resourcePath) {
		return asLink(urlFactory.buildUrl(service, resourcePath), relation);
	}

	public Link buildUrl(String relation, Service service, ContextPathSection resourcePath, final Parameter... parameters) {
		return asLink(urlFactory.buildUrl(service, resourcePath, parameters), relation);
	}

	public Link buildUrl(String relation, Service service, ContextPathSection resourcePath, final ParameterGroup... groups) {
		return asLink(urlFactory.buildUrl(service, resourcePath, groups), relation);
	}

	public Link asLink(final String relation, final String url) {
		return new Link(url, relation);
	}
}