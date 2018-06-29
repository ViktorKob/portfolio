package net.thomas.portfolio.shared_objects.hbase_index.transformation;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public interface IndexStep {
	void executeAndUpdateIndex(HbaseIndexSchema schema, World world, HbaseIndex partiallyConstructedIndex);
}
