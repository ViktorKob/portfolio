package net.thomas.portfolio.graphql.fetchers.fields.primitive;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.hbase_index.GraphQlUtilities;
import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class TimestampFieldDataFetcher extends ModelDataFetcher<String> {
	private final String fieldName;
	private final DateConverter dateFormatter;

	public TimestampFieldDataFetcher(String fieldName, HbaseModelAdaptor adaptor, GraphQlUtilities utilities) {
		super(adaptor, 0);
		this.fieldName = fieldName;
		dateFormatter = utilities.getDateConverter();
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final Datatype entity = (Datatype) environment.getSource();
		final Long timestamp = (Long) entity.get(fieldName);
		return dateFormatter.formatTimestamp(timestamp);
	}
}