package net.thomas.portfolio.hbase_index.schema.simple_rep.parsers;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.hbase_index.schema.util.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StringFieldSimpleRepParser extends SimpleRepresentationParserImpl {
	private final String field;

	public static StringFieldSimpleRepParser newStringFieldParser(String type, String field, IdCalculator idCalculator) {
		return new StringFieldSimpleRepParser(type, field, ".+$", idCalculator);
	}

	public static StringFieldSimpleRepParser newStringFieldParser(String type, String field, String pattern, IdCalculator idCalculator) {
		return new StringFieldSimpleRepParser(type, field, pattern, idCalculator);
	}

	private StringFieldSimpleRepParser(String type, String field, String pattern, IdCalculator idCalculator) {
		super(type, pattern, idCalculator);
		this.field = field;
	}

	@Override
	protected void populateValues(DataType entity, String simpleRepresentation) {
		entity.put(field, simpleRepresentation);
	}

	public String getField() {
		return field;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}