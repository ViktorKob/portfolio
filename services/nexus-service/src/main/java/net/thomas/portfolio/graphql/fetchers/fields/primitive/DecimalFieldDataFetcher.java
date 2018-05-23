package net.thomas.portfolio.graphql.fetchers.fields.primitive;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class DecimalFieldDataFetcher extends ModelDataFetcher<Double> {
	private final String fieldName;

	public DecimalFieldDataFetcher(String fieldName, HbaseModelAdaptor adaptor) {
		super(adaptor, 0);
		this.fieldName = fieldName;
	}

	@Override
	public Double _get(DataFetchingEnvironment environment) {
		final Datatype entity = (Datatype) environment.getSource();
		final Object value = entity.get(fieldName);
		if (value instanceof Double) {
			return (Double) value;
		} else if (value instanceof Float) {
			return Double.valueOf((float) value);
		} else {
			return Double.valueOf(value.toString());
		}
	}
}