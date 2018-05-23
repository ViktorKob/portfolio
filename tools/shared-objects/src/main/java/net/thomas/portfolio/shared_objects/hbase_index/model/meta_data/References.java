package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import java.util.LinkedList;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class References extends LinkedList<Reference> {
	private static final long serialVersionUID = 1L;

	public References() {
		super();
	}

	public References(LinkedList<Reference> references) {
		super();
		addAll(references);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}