package net.thomas.portfolio.hbase_index.fake.world;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

public interface ProcessingStep {
	void executeAndUpdateIndex(WorldAccess world, HbaseIndex partiallyConstructedIndex);
}
