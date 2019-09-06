package net.thomas.portfolio.service_commons.network.urls;

@FunctionalInterface
public interface UrlPrefixBuilder {
	/***
	 * @return URL prefix without ending /
	 */
	String build();
}
