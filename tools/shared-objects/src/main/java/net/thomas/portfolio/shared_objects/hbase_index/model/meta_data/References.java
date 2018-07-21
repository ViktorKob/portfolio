package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class References {
	private Collection<Reference> references;

	public References() {
		references = emptyList();
	}

	public References(Collection<Reference> references) {
		this.references = references;
	}

	public Collection<Reference> getReferences() {
		return references;
	}

	public void setReferences(Collection<Reference> references) {
		this.references = references;
	}

	public int size() {
		return references.size();
	}

	@Override
	public int hashCode() {
		return references.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return references.equals(obj);
	}

	@Override
	public String toString() {
		return references.toString();
	}
}