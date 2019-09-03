package net.thomas.portfolio.service_commons.network;

@FunctionalInterface
public interface UrlPrefixBuilder {
	/***
	 * @return URL prefix without ending /
	 */
	String build();
}
