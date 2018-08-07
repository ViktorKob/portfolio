package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import graphql.GraphQLException;
import net.thomas.portfolio.nexus.graphql.fetchers.ClientException;

@ClientException
public class IllegalLookupException extends GraphQLException {
	private static final long serialVersionUID = 1L;

	public IllegalLookupException(String message) {
		super(message);
	}
}