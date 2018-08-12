package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep;

import static java.util.regex.Pattern.compile;
import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.hbase_index.schema.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParser;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PositiveIntegerFieldSimpleRepParser extends SimpleRepresentationParser {
	private final String field;
	private final Pattern removableCharactersPattern;

	public static PositiveIntegerFieldSimpleRepParser newPositiveIntegerFieldParser(String type, String field, IdCalculator idCalculator) {
		return new PositiveIntegerFieldSimpleRepParser(type, field, "[\\d\\s]+$", idCalculator);
	}

	public static PositiveIntegerFieldSimpleRepParser newPositiveIntegerFieldParser(String type, String field, String pattern, IdCalculator idCalculator) {
		return new PositiveIntegerFieldSimpleRepParser(type, field, pattern, idCalculator);
	}

	private PositiveIntegerFieldSimpleRepParser(String type, String field, String pattern, IdCalculator idCalculator) {
		super(type, pattern, idCalculator);
		this.field = field;
		removableCharactersPattern = compile("^\\D");
	}

	@Override
	protected void populateValues(DataType entity, String source) {
		source = removableCharactersPattern.matcher(source)
			.replaceAll("");
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
		if (!(obj instanceof PositiveIntegerFieldSimpleRepParser)) {
			return false;
		}
		final PositiveIntegerFieldSimpleRepParser other = (PositiveIntegerFieldSimpleRepParser) obj;
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
