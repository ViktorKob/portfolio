package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import graphql.GraphQLException;
import net.thomas.portfolio.nexus.graphql.fetchers.ClientException;

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