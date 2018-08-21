package net.thomas.portfolio.hbase_index.fake.world.storage;

import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;

public interface EventReader extends Iterable<Event> {
	Event getEvent(String eventUid);

	References getReferences(final String eventUid);
}