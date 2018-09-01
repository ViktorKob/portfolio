package net.thomas.portfolio.hbase_index.schema.simple_rep.library;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.hbase_index.schema.simple_rep.SimpleRepresentationParserLibrary;
import net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.SimpleRepresentationParserImpl;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SimpleRepresentationParserLibraryImpl implements SimpleRepresentationParserLibrary {
	private Collection<String> selectorTypes;
	protected Map<String, SimpleRepresentationParserImpl> parsers;

	public SimpleRepresentationParserLibraryImpl(Map<String, SimpleRepresentationParserImpl> parsers) {
		this.parsers = parsers;
		for (final SimpleRepresentationParserImpl parser : parsers.values()) {
			parser.setLibrary(this);
		}
	}

	@Override
	public void setSelectorTypes(Collection<String> selectorTypes) {
		this.selectorTypes = selectorTypes;
	}

	@Override
	public List<Selector> getSelectorSuggestions(String selectorString) {
		final List<Selector> selectors = new LinkedList<>();
		for (final String selectorType : selectorTypes) {
			try {
				final Selector selector = parse(selectorType, selectorString);
				if (selector != null) {
					selectors.add(selector);
				}
			} catch (final Throwable t) {
				// Ignored
			}
		}
		return selectors;
	}

	@Override
	public boolean hasValidFormat(String source) {
		for (final SimpleRepresentationParserImpl parser : parsers.values()) {
			if (parser.hasValidFormat(source)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Selector parse(String type, String simpleRepresenation) {
		if (parsers.containsKey(type)) {
			final SimpleRepresentationParserImpl parser = parsers.get(type);
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