package net.thomas.portfolio.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.hbase_index.GraphQlUtilities;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class FormattedTimeOfInterceptionDataFetcher extends FormattedTimestampDataFetcher {

	public FormattedTimeOfInterceptionDataFetcher(HbaseModelAdaptor adaptor, GraphQlUtilities utilities) {
		super(adaptor, utilities);
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final Document document = environment.getSource();
		if (environment.getArgument("format") != null) {
			return formatTimestamp(environment.getArgument("format")
				.toString(), document.getTimeOfInterception());
		} else {
			return formatTimestamp(null, document.getTimeOfInterception());
		}
	}
}