package net.thomas.portfolio.hbase_index.fake;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.thomas.portfolio.hbase_index.fake.world.storage.EventReader;
import net.thomas.portfolio.hbase_index.fake.world.storage.EventWriter;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;

public class FakeWorldStorage implements EventWriter, EventReader {
	private final Map<String, Event> events;
	private final Map<String, References> references;

	public FakeWorldStorage() {
		events = new HashMap<>();
		references = new HashMap<>();
	}

	@Override
	public Iterator<Event> iterator() {
		return events.values().iterator();
	}

	@Override
	public void add(Event event) {
		events.put(event.uid, event);
	}

	@Override
	public void add(String eventUid, References references) {
		this.references.put(eventUid, references);
	}

	@Override
	public Event getEvent(String eventUid) {
		return events.get(eventUid);
	}

	@Override
	public References getReferences(final String eventUid) {
		return references.get(eventUid);
	}
}