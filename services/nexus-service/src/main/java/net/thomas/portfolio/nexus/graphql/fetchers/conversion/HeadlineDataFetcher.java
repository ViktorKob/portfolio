package net.thomas.portfolio.nexus.graphql.fetchers.conversion;

import static net.thomas.portfolio.shared_objects.usage_data.UsageActivityType.READ_DOCUMENT;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

public class HeadlineDataFetcher extends ModelDataFetcher<String> {

	public HeadlineDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final Object entity = environment.getSource();
		if (entity instanceof DataType) {
			return usageLogAndRenderHeadline(((DataType) entity).getId());
		} else if (entity instanceof DataTypeId) {
			return usageLogAndRenderHeadline((DataTypeId) entity);
		} else if (entity instanceof DocumentInfo) {
			return usageLogAndRenderHeadline(((DocumentInfo) entity).getId());
		} else {
			throw new RuntimeException("Unable to convert data type of type " + entity.getClass()
				.getSimpleName());
		}
	}

	private String usageLogAndRenderHeadline(final DataTypeId id) {
		if (adaptors.isDocument(id.type)) {
			adaptors.storeUsageActivity(id, new UsageActivity("Tester", READ_DOCUMENT, System.currentTimeMillis()));
		}
		return adaptors.renderAsText(id);
	}
}