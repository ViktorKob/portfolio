package net.thomas.portfolio.hbase_index.fake.world;

import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;

public interface WorldAccess extends Iterable<Event> {
	Event getEvent(String uid);

	References getReferences(String uid);
}