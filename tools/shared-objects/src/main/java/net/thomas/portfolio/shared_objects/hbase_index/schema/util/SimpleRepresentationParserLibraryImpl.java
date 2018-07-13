package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.Map;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SimpleRepresentationParserLibraryImpl implements SimpleRepresentationParserLibrary {
	protected Map<String, SimpleRepresentationParser> parsers;

	public SimpleRepresentationParserLibraryImpl(Map<String, SimpleRepresentationParser> parsers) {
		this.parsers = parsers;
	}

	@Override
	public boolean hasValidFormat(String source) {
		for (final SimpleRepresentationParser parser : parsers.values()) {
			if (parser.hasValidFormat(source)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Selector parse(String type, String simpleRepresenation) {
		if (parsers.containsKey(type)) {
			final SimpleRepresentationParser parser = parsers.get(type);
			if (parser.hasValidFormat(simpleRepresenation)) {
				return parser.parse(type, simpleRepresenation);
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}