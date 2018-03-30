package net.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;
import net.model.types.GeoLocation;

public class GeoLocationFieldDataFetcher extends ModelDataFetcher<GeoLocation> {
	private final String fieldName;

	public GeoLocationFieldDataFetcher(String fieldName, ModelAdaptor adaptor) {
		super(adaptor, 0);
		this.fieldName = fieldName;
	}

	@Override
	public GeoLocation _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return (GeoLocation) entity.get(fieldName);
	}
}