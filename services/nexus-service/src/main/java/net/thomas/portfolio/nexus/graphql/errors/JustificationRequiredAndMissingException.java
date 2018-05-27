package net.thomas.portfolio.nexus.graphql.errors;

import static graphql.ErrorType.ValidationError;

import java.util.List;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

public class JustificationRequiredAndMissingException implements GraphQLError {

	private final String message;

	public JustificationRequiredAndMissingException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public List<SourceLocation> getLocations() {
		return null;
	}

	@Override
	public ErrorType getErrorType() {
		return ValidationError;
	}
}
