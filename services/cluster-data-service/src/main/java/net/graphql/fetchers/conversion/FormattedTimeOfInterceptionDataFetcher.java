package net.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.model.types.Document;

public class FormattedTimeOfInterceptionDataFetcher extends FormattedTimestampDataFetcher {

	public FormattedTimeOfInterceptionDataFetcher(ModelAdaptor adaptor) {
		super(adaptor);
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final Document document = environment.getSource();
		if (environment.getArgument("format") != null) {
			return formatTimestamp(environment.getArgument("format").toString(), document.getTimeOfInterception());
		} else {
			return formatTimestamp(null, document.getTimeOfInterception());
		}
	}
}