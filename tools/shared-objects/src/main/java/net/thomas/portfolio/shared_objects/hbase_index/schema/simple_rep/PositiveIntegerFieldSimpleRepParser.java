package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep;

import static java.util.regex.Pattern.compile;
import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.regex.Pattern;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParser;

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
	public String toString() {
		return asString(this);
	}
}
