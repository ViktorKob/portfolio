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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (parsers == null ? 0 : parsers.hashCode());
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
		if (!(obj instanceof SimpleRepresentationParserLibraryImpl)) {
			return false;
		}
		final SimpleRepresentationParserLibraryImpl other = (SimpleRepresentationParserLibraryImpl) obj;
		if (parsers == null) {
			if (other.parsers != null) {
				return false;
			}
		} else if (!parsers.equals(other.parsers)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}