package net.thomas.portfolio.nexus.graphql.fetchers;

import graphql.GraphQLException;

@ClientException
public class IllegalLookupException extends GraphQLException {
	private static final long serialVersionUID = 1L;

	public IllegalLookupException(String message) {
		super(message);
	}
}