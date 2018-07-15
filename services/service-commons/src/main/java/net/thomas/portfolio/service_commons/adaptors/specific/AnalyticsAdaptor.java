package net.thomas.portfolio.service_commons.adaptors.specific;

import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

/***
 * This adaptor allows usage of endpoints in the Analytics service as java methods.
 */
public interface AnalyticsAdaptor {

	/***
	 * This method will run a query in the analytics system to fetch relevant information about the selector.
	 *
	 * @param selectorId
	 *            The ID of the selector to query about
	 * @return A summary of the knowledge about this exact selector present in the system
	 */
	AnalyticalKnowledge getKnowledge(DataTypeId selectorId);

}
