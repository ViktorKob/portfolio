package net.thomas.portfolio.nexus.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;

public class SimpleRepresentationDataFetcher extends ModelDataFetcher<String> {

	public SimpleRepresentationDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String get(DataFetchingEnvironment environment) {
		if (environment.getSource() == null) {
			return null;
		}
		return adaptors.renderAsSimpleRepresentation(getId(environment));
	}
}