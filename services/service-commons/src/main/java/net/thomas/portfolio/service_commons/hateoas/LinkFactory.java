package net.thomas.portfolio.service_commons.hateoas;

import org.springframework.hateoas.Link;

import net.thomas.portfolio.hateoas.PortfolioUrlFactory.UrlGenerator;

public class LinkFactory {
	public static Link asLink(final String relation, final String url) {
		return new Link(url, relation);
	}

	public static Link asLink(final String relation, UrlGenerator urlGenerator) {
		return new Link(urlGenerator.generate(), relation);
	}
}
