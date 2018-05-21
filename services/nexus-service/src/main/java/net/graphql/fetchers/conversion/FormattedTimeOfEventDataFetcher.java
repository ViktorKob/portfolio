package net.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.model.types.Document;

public class FormattedTimeOfEventDataFetcher extends FormattedTimestampDataFetcher {

	public FormattedTimeOfEventDataFetcher(ModelAdaptor adaptor) {
		super(adaptor);
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final Document document = environment.getSource();
		if (environment.getArgument("format") != null) {
			return formatTimestamp(environment.getArgument("format").toString(), document.getTimeOfEvent());
		} else {
			return formatTimestamp(null, document.getTimeOfEvent());
		}
	}
}