package net.thomas.portfolio.nexus.graphql.arguments;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;

public interface ArgumentExtractor {
	public void initialize(Adaptors adaptors);

	public <T> T extract(GraphQlArgument argument, DataFetchingEnvironment environment);

}
