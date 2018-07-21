package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Entities {
	private Collection<DataType> entities;

	public Entities() {
		entities = emptyList();
	}

	public Entities(Collection<DataType> entities) {
		this.entities = entities;
	}

	public Collection<DataType> getEntities() {
		return entities;
	}

	public void setEntities(Collection<DataType> entities) {
		this.entities = entities;
	}

	public int size() {
		return entities.size();
	}

	@Override
	public int hashCode() {
		return entities.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return entities.equals(obj);
	}

	@Override
	public String toString() {
		return entities.toString();
	}
}