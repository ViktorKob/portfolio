package net.model.util;

import java.util.Collection;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.model.DataType;
import net.model.data.Field;
import net.model.types.Selector;

public abstract class SampleModelSimpleRepresentationParser<PARSE_TYPE> implements Parser<PARSE_TYPE, Selector> {
	private final UidGenerator uidTool;

	public SampleModelSimpleRepresentationParser(Collection<Field> fields) {
		uidTool = new UidGenerator(fields, false);
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
