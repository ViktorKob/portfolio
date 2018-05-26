package net.thomas.portfolio.shared_objects.adaptors;

import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public interface AnalyticsAdaptor {
	PriorKnowledge getPriorKnowledge(DataTypeId selectorId);
}
