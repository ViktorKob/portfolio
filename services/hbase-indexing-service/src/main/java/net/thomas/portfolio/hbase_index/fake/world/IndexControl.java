package net.thomas.portfolio.hbase_index.fake.world;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public interface IndexControl {

	void index(WorldAccess world);

	HbaseIndexSchema getSchema();

	HbaseIndex getIndex();

}
