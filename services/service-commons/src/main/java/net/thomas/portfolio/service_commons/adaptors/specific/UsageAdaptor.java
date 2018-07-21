package net.thomas.portfolio.service_commons.adaptors.specific;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

public interface UsageAdaptor {
	/***
	 * @param documentId
	 *            The ID of the document that has been used
	 * @param activity
	 *            A description of who used the document how, when
	 * @return A copy of the activity entity as it was stored
	 */
	UsageActivity storeUsageActivity(DataTypeId documentId, UsageActivity activity);

	/***
	 * @param documentId
	 *            The ID of the document to fetch usage events for
	 * @param bounds
	 *            The parameters for the fetch
	 * @return An ordered (newest first) list of events for the document
	 */
	UsageActivities fetchUsageActivities(DataTypeId documentId, Bounds bounds);
}