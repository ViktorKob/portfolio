package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import static java.util.regex.Pattern.compile;
import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.Parser;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = ParserDeserializer.class)
public abstract class SimpleRepresentationParser implements Parser<String, Selector> {
	private final String type;
	private final Pattern pattern;
	private final IdCalculator idCalculator;
	protected SimpleRepresentationParserLibrary library;

	public SimpleRepresentationParser(String type, String pattern, IdCalculator idCalculator) {
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
	public boolean hasValidFormat(String source) {
		return pattern.matcher(source)
			.matches();
	}

	@Override
	public Selector parse(String type, String source) {
		final Selector selector = new Selector();
		populateValues(selector, source);
		populateUid(selector, type);
		return selector;
	}

	protected abstract void populateValues(DataType entity, String source);

	protected void populateUid(final Selector selector, String type) {
		selector.setId(idCalculator.calculate(type, selector));
	}

	public abstract String getImplementationClass();

	public String getPattern() {
		return pattern.toString();
	}

	public IdCalculator getIdCalculator() {
		return idCalculator;
	}

	@JsonIgnore
	public SimpleRepresentationParserLibrary getLibrary() {
		return library;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}
