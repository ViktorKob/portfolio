package net.thomas.portfolio.nexus.graphql.fetchers;

import graphql.GraphQLException;

@ClientException
public class IllegalLookupException extends GraphQLException {
	private static final long serialVersionUID = 1L;

	public IllegalLookupException(final String message) {
		super(message);
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return new StackTraceElement[] { super.getStackTrace()[0] };
	}
}