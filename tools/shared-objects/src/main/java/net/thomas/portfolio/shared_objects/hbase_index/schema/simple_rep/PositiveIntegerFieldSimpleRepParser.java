package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep;

import static java.util.regex.Pattern.compile;
import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParser;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PositiveIntegerFieldSimpleRepParser extends SimpleRepresentationParser {
	private final String field;
	private final Pattern invalidCharactersPattern;

	public static PositiveIntegerFieldSimpleRepParser newPositiveIntegerFieldParser(String type, String field, IdCalculator idCalculator) {
		return new PositiveIntegerFieldSimpleRepParser(type, field, "[\\d\\s]+$", idCalculator);
	}

	public static PositiveIntegerFieldSimpleRepParser newPositiveIntegerFieldParser(String type, String field, String pattern, IdCalculator idCalculator) {
		return new PositiveIntegerFieldSimpleRepParser(type, field, pattern, idCalculator);
	}

	private PositiveIntegerFieldSimpleRepParser(String type, String field, String pattern, IdCalculator idCalculator) {
		super(type, pattern, idCalculator);
		this.field = field;
		invalidCharactersPattern = compile("^\\D");
	}

	@Override
	protected void populateValues(DataType entity, String source) {
		source = invalidCharactersPattern.matcher(source)
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
	public String toString() {
		return asString(this);
	}
}
