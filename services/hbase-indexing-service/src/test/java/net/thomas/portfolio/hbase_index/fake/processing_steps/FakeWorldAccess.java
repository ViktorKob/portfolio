package net.thomas.portfolio.hbase_index.fake.processing_steps;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Iterator;
import java.util.Map;

import net.thomas.portfolio.hbase_index.fake.world.World;
import net.thomas.portfolio.hbase_index.fake.world.WorldAccess;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;

public class FakeWorldAccess implements WorldAccess {
	private final World world;
	private final Map<String, Event> events;

	public FakeWorldAccess(final World world) {
		this.world = world;
		events = world.getEvents().stream().collect(toMap(event -> event.uid, identity()));
	}

	@Override
	public Iterator<Event> iterator() {
		return world.getEvents().iterator();
	}

	@Override
	public Event getEvent(final String uid) {
		return events.get(uid);
	}

	@Override
	public References getReferences(final String uid) {
		return world.getSourceReferences().get(uid);
	}
}
