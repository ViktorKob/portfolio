package net.thomas.portfolio.nexus.graphql.exceptions;

import graphql.GraphQLException;

@ClientException
public class InvalidDateArgumentException extends GraphQLException {
	private static final long serialVersionUID = 1L;

	public InvalidDateArgumentException(final String message) {
		super(message);
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return new StackTraceElement[] { super.getStackTrace()[0] };
	}
}