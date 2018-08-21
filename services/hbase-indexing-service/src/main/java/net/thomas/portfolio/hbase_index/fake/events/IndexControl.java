package net.thomas.portfolio.hbase_index.fake.events;

import net.thomas.portfolio.hbase_index.fake.world.storage.EventReader;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public interface IndexControl {

	void index(final EventReader events);

	HbaseIndexSchema getSchema();

	HbaseIndex getIndex();

}
