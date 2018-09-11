package net.thomas.portfolio.nexus.service;

import static graphql.execution.ExecutionPath.rootPath;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.servlet.DefaultGraphQLErrorHandler;
import net.thomas.portfolio.nexus.graphql.exceptions.ClientException;

public class CustomErrorHandler extends DefaultGraphQLErrorHandler {
	@Override
	protected boolean isClientError(final GraphQLError error) {
		return isClientException(error) || super.isClientError(error);
	}

	private boolean isClientException(final GraphQLError error) {
		if (error instanceof ExceptionWhileDataFetching) {
			return isClientException((ExceptionWhileDataFetching) error);
		} else {
			return false;
		}
	}

	private boolean isClientException(final ExceptionWhileDataFetching error) {
		final Throwable exception = error.getException();
		final Class<? extends Throwable> exceptionClass = exception.getClass();
		return exceptionClass.isAnnotationPresent(ClientException.class);
	}

	@Override
	protected List<GraphQLError> filterGraphQLErrors(final List<GraphQLError> errors) {
		return errors.stream()
				.filter(this::isClientError)
				.map(error -> isClientException(error) ? new SanitizedError((ExceptionWhileDataFetching) error) : error)
				.distinct()
				.collect(toList());
	}

	private static class SanitizedError extends ExceptionWhileDataFetching {
		private static final long serialVersionUID = 1L;

		public SanitizedError(final ExceptionWhileDataFetching error) {
			super(rootPath(), error.getException(), null);
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof CustomErrorHandler.SanitizedError) {
				return getMessage().equals(((CustomErrorHandler.SanitizedError) o).getMessage());
			}
			return false;
		}

		@Override
		@JsonIgnore
		public Throwable getException() {
			return super.getException();
		}
	}
}