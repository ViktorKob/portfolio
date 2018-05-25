package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import java.util.Collection;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.IdGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.Parser;

public abstract class SimpleRepresentationParser<PARSE_TYPE> implements Parser<PARSE_TYPE, Selector> {
	private final IdGenerator idTool;

	public SimpleRepresentationParser(Collection<Field> fields) {
		idTool = new IdGenerator(fields, false);
	}

	@Override
	public Selector parse(String type, PARSE_TYPE source) {
		final Selector entity = new Selector();
		populateValues(entity, source);
		populateUid(entity, type);
		return entity;
	}

	protected abstract void populateValues(DataType entity, PARSE_TYPE source);

	protected void populateUid(final Selector sample, String type) {
		sample.setId(idTool.calculateId(type, sample));
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
