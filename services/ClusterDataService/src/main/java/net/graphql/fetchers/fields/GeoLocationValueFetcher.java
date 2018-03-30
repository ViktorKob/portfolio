package net.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.types.GeoLocation;

public class GeoLocationValueFetcher extends ModelDataFetcher<Double> {
	private final String fieldName;

	public GeoLocationValueFetcher(String fieldName, ModelAdaptor adaptor) {
		super(adaptor, 0);
		this.fieldName = fieldName;
	}

	@Override
	public Double _get(DataFetchingEnvironment environment) {
		final GeoLocation location = (GeoLocation) environment.getSource();
		if ("longitude".equals(fieldName)) {
			return location.longitude;
		} else if ("latitude".equals(fieldName)) {
			return location.latitude;
		}
		return null;
	}
}