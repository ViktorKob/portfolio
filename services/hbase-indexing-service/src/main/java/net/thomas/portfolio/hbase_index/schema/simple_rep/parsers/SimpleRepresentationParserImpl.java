package net.thomas.portfolio.hbase_index.schema.simple_rep.parsers;

import static java.util.regex.Pattern.compile;
import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.regex.Pattern;

import net.thomas.portfolio.hbase_index.schema.simple_rep.SimpleRepresentationParser;
import net.thomas.portfolio.hbase_index.schema.simple_rep.SimpleRepresentationParserLibrary;
import net.thomas.portfolio.hbase_index.schema.util.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public abstract class SimpleRepresentationParserImpl implements SimpleRepresentationParser<String, Selector> {
	private final String type;
	private final Pattern pattern;
	private final IdCalculator idCalculator;
	protected SimpleRepresentationParserLibrary library;

	public SimpleRepresentationParserImpl(String type, String pattern, IdCalculator idCalculator) {
		this.type = type;
		this.pattern = compile(pattern);
		this.idCalculator = idCalculator;
	}

	public void setLibrary(SimpleRepresentationParserLibrary library) {
		this.library = library;
	}

	public String getType() {
		return type;
	}

	@Override
	public boolean hasValidFormat(String simpleRepresentation) {
		return pattern.matcher(simpleRepresentation).matches();
	}

	@Override
	public Selector parse(String type, String simpleRepresentation) {
		final Selector selector = new Selector();
		populateValues(selector, simpleRepresentation);
		populateUid(selector, type);
		return selector;
	}

	protected abstract void populateValues(DataType entity, String simpleRepresentation);

	protected void populateUid(final Selector selector, String type) {
		selector.setId(idCalculator.calculate(type, selector));
	}

	@Override
	public String toString() {
		return asString(this);
	}
}
