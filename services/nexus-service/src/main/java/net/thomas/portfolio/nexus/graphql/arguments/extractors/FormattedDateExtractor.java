package net.thomas.portfolio.nexus.graphql.arguments.extractors;

import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.arguments.ArgumentExtractor;
import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter;

public class FormattedDateExtractor implements ArgumentExtractor {

	private DateConverter dateConverter;

	@Override
	public void initialize(Adaptors adaptors) {
		dateConverter = adaptors.getIec8601DateConverter();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T extract(GraphQlArgument argument, DataFetchingEnvironment environment) {
		if (environment.containsArgument(argument.getName())) {
			final String date = environment.getArgument(argument.getName());
			try {
				return (T) (Long) dateConverter.parseTimestamp(date);
			} catch (final RuntimeException e) {
				throw new GraphQLException("Unable to parse date " + date + " from argument " + argument.getName(), e);
			}
		}
		return null;
	}
}