package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.IdGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.Parser;

public abstract class SimpleRepresentationParser<PARSE_TYPE> implements Parser<PARSE_TYPE, Selector> {
	private final IdGenerator idTool;

	public SimpleRepresentationParser(List<Field> fields) {
		idTool = new IdGenerator(fields, false);
	}

	@Override
	public Selector parse(String type, PARSE_TYPE source) {
		final Selector selector = new Selector();
		populateValues(selector, source);
		populateUid(selector, type);
		return selector;
	}

	protected abstract void populateValues(DataType entity, PARSE_TYPE source);

	protected void populateUid(final Selector selector, String type) {
		selector.setId(idTool.calculateId(type, selector));
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
