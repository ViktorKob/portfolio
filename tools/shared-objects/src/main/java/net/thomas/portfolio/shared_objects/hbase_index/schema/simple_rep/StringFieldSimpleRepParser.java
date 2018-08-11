package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.hbase_index.schema.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (field == null ? 0 : field.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof StringFieldSimpleRepParser)) {
			return false;
		}
		final StringFieldSimpleRepParser other = (StringFieldSimpleRepParser) obj;
		if (field == null) {
			if (other.field != null) {
				return false;
			}
		} else if (!field.equals(other.field)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}