package net.thomas.portfolio.hbase_index.fake.events;

import net.thomas.portfolio.hbase_index.fake.world.storage.EventReader;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

public interface ProcessingStep {
	void executeAndUpdateIndex(EventReader events, HbaseIndex partiallyConstructedIndex);
}