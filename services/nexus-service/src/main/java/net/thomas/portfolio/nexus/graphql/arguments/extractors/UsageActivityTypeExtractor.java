package net.thomas.portfolio.nexus.graphql.arguments.extractors;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.arguments.ArgumentExtractor;
import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;

public class UsageActivityTypeExtractor implements ArgumentExtractor {

	@Override
	public void initialize(Adaptors adaptors) {
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T extract(GraphQlArgument argument, DataFetchingEnvironment environment) {
		if (environment.containsArgument(argument.getName())) {
			return (T) UsageActivityType.valueOf(environment.getArgument("activityType"));
		} else {
			return null;
		}
	}
}