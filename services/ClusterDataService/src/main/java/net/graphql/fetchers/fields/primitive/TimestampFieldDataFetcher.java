package net.graphql.fetchers.fields.primitive;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;
import net.model.util.DateConverter;

public class TimestampFieldDataFetcher extends ModelDataFetcher<String> {
	private final String fieldName;
	private final DateConverter dateFormatter;

	public TimestampFieldDataFetcher(String fieldName, ModelAdaptor adaptor) {
		super(adaptor, 0);
		this.fieldName = fieldName;
		dateFormatter = adaptor.getDateConverter();
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		final Long timestamp = (Long) entity.get(fieldName);
		return dateFormatter.formatTimestamp(timestamp);
	}
}