package net.thomas.portfolio.hbase_index.schema.simple_rep.parsers;

import static java.util.regex.Pattern.compile;
import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.hbase_index.schema.util.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PositiveIntegerFieldSimpleRepParser extends SimpleRepresentationParserImpl {
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
	protected void populateValues(DataType entity, String simpleRepresentation) {
		simpleRepresentation = removableCharactersPattern.matcher(simpleRepresentation).replaceAll("");
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
