package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "A collection of entities from the index")
public class Entities {
	@ApiModelProperty("The collection of entities")
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

	public boolean hasData() {
		return !entities.isEmpty();
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