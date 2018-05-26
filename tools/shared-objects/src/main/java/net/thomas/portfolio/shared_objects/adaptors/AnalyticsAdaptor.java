package net.thomas.portfolio.shared_objects.adaptors;

import net.thomas.portfolio.shared_objects.analytics.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public interface AnalyticsAdaptor {
	PreviousKnowledge getPreviousKnowledgeFor(DataTypeId selectorId);
}
