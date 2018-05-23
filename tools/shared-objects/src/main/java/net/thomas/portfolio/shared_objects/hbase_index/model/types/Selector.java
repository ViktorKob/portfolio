package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class Selector extends DataType {
	private static final long serialVersionUID = 1L;

	public Selector(String type) {
		super(type);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}