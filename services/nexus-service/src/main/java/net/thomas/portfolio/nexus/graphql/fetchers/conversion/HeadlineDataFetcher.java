package net.thomas.portfolio.nexus.graphql.fetchers.conversion;

import static net.thomas.portfolio.nexus.graphql.fetchers.GlobalServiceArgumentId.USER_ID;
import static net.thomas.portfolio.shared_objects.usage_data.UsageActivityType.READ_DOCUMENT;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

public class HeadlineDataFetcher extends ModelDataFetcher<String> {

	public HeadlineDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String get(DataFetchingEnvironment environment) {
		final DataTypeId id = getId(environment);
		if (adaptors.isDocument(id.type)) {
			String user = (String) getProxy(environment).get(USER_ID);
			if (user == null) {
				user = "Unspecified user";
			}
			adaptors.storeUsageActivity(id, new UsageActivity(user, READ_DOCUMENT, System.currentTimeMillis()));
		}
		return adaptors.renderAsText(id);
	}
}