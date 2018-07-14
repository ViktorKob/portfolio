package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParser;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StringFieldSimpleRepParser extends SimpleRepresentationParser {
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
	protected void populateValues(DataType entity, String source) {
		entity.put(field, source);
	}

	@Override
	public String getImplementationClass() {
		return getClass().getSimpleName();
	}

	public String getField() {
		return field;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}