package net.thomas.portfolio.nexus.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;

public class GeoLocationFieldDataFetcher extends ModelDataFetcher<GeoLocation> {
	private final String fieldName;

	public GeoLocationFieldDataFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
		this.fieldName = fieldName;
	}

	@Override
	public GeoLocation _get(DataFetchingEnvironment environment) {
		final DataType entity = extractOrFetchDataType(environment);
		return (GeoLocation) entity.get(fieldName);
	}
}