package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.shared_objects.hbase_index.model.DataTypeType.SELECTOR;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

@JsonDeserialize(as = Selector.class)
public class Selector extends DataType {
	private static final long serialVersionUID = 1L;

	public Selector() {
	}

	public Selector(String type) {
		super(SELECTOR, type);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}