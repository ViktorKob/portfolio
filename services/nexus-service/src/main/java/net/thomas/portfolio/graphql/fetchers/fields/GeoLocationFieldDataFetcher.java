package net.thomas.portfolio.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public class GeoLocationFieldDataFetcher extends ModelDataFetcher<GeoLocation> {
	private final String fieldName;

	public GeoLocationFieldDataFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
		this.fieldName = fieldName;
	}

	@Override
	public GeoLocation _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return (GeoLocation) entity.get(fieldName);
	}
}