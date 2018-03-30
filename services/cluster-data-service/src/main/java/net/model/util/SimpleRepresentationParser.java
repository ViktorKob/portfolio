package net.model.util;

import static net.sample.SampleModel.DATA_TYPE_FIELDS;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.model.DataType;
import net.model.types.Selector;

public abstract class SimpleRepresentationParser<PARSE_TYPE> implements Parser<PARSE_TYPE, Selector> {
	private final UidTool uidTool;

	public SimpleRepresentationParser(String type) {
		uidTool = new UidTool(DATA_TYPE_FIELDS.get(type).values(), false);
	}

	@Override
	public Selector parse(String type, PARSE_TYPE source) {
		final Selector entity = new Selector(type);
		populateValues(entity, source);
		populateUid(entity);
		return entity;
	}

	protected abstract void populateValues(DataType entity, PARSE_TYPE source);

	protected void populateUid(final Selector sample) {
		sample.setUid(uidTool.calculateUid(sample));
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
