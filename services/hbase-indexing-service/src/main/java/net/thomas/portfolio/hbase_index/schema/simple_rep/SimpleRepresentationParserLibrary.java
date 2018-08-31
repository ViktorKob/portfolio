package net.thomas.portfolio.hbase_index.schema.simple_rep;

import java.util.Collection;
import java.util.List;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public interface SimpleRepresentationParserLibrary extends SimpleRepresentationParser<String, Selector> {

	void setSelectorTypes(Collection<String> selectorTypes);

	List<Selector> getSelectorSuggestions(String selectorString);
}