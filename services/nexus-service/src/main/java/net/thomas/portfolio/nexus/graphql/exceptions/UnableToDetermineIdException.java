package net.thomas.portfolio.nexus.graphql.exceptions;

import graphql.GraphQLException;

@ClientException
public class UnableToDetermineIdException extends GraphQLException {
	private static final long serialVersionUID = 1L;

	public UnableToDetermineIdException(final String message) {
		super(message);
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return new StackTraceElement[] { super.getStackTrace()[0] };
	}
}