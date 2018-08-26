package net.thomas.portfolio.nexus.graphql.arguments.extractors;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.arguments.ArgumentExtractor;
import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument;
import net.thomas.portfolio.nexus.graphql.exceptions.InvalidDateArgumentException;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter;

public class FormattedDateExtractor implements ArgumentExtractor {

	private DateConverter dateConverter;

	@Override
	public void initialize(final Adaptors adaptors) {
		dateConverter = adaptors.getIec8601DateConverter();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T extract(final GraphQlArgument argument, final DataFetchingEnvironment environment) {
		if (environment.containsArgument(argument.getName())) {
			final String date = environment.getArgument(argument.getName());
			final String decodedDate = date.replaceAll(" ", "+"); // TODO[Thomas]: Hack, due to + not being encoded by UriComponentsBuilder
			try {
				return (T) (Long) dateConverter.parse(decodedDate);
			} catch (final Exception e) {
				throw new InvalidDateArgumentException("Unable to parse a valid date from value '" + date + "' for argument " + argument.getName());
			}
		}
		return null;
	}
}