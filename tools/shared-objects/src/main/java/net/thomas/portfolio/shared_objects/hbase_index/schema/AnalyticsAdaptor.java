package net.thomas.portfolio.shared_objects.hbase_index.schema;

import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public interface AnalyticsAdaptor {
	PreviousKnowledge getPreviousKnowledgeFor(Selector selector);
}
