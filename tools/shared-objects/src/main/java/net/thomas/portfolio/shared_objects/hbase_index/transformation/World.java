package net.thomas.portfolio.shared_objects.hbase_index.transformation;

import java.util.Collection;
import java.util.Map;

import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

public class World {
	private Collection<DataType> events;
	private Map<String, Collection<Reference>> sourceReferences;

	public World() {
	}

	public void setEvents(Collection<DataType> events) {
		this.events = events;
	}

	public void setSourceReferences(Map<String, Collection<Reference>> sourceReferences) {
		this.sourceReferences = sourceReferences;
	}

	public Collection<DataType> getEvents() {
		return events;
	}

	public Map<String, Collection<Reference>> getSourceReferences() {
		return sourceReferences;
	}
}