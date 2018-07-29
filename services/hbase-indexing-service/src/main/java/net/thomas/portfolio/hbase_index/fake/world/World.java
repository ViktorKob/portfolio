package net.thomas.portfolio.hbase_index.fake.world;

import java.util.Collection;
import java.util.Map;

import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;

public class World {
	private Collection<Event> events;
	private Map<String, References> sourceReferences;

	public World() {
	}

	public void setEvents(Collection<Event> events) {
		this.events = events;
	}

	public void setSourceReferences(Map<String, References> sourceReferences) {
		this.sourceReferences = sourceReferences;
	}

	public Collection<Event> getEvents() {
		return events;
	}

	public Map<String, References> getSourceReferences() {
		return sourceReferences;
	}
}