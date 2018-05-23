package net.thomas.portfolio.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class GeoLocationValueFetcher extends ModelDataFetcher<Double> {
	private final String fieldName;

	public GeoLocationValueFetcher(String fieldName, HbaseModelAdaptor adaptor) {
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