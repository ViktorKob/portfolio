package net.thomas.portfolio.hbase_index.schema.util;

import java.util.Collection;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.Parser;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.UidGenerator;

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

	protected abstract void populateValues(Datatype entity, PARSE_TYPE source);

	protected void populateUid(final Selector sample) {
		sample.setUid(uidTool.calculateUid(sample));
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
