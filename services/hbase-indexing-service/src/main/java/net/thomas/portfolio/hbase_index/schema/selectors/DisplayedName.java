package net.thomas.portfolio.hbase_index.schema.selectors;

import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.annotations.SimpleRepresentable;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.StringFieldSimpleRepParser;

@SimpleRepresentable(parser = StringFieldSimpleRepParser.class, field = "name")
public class DisplayedName extends SelectorEntity {
	@PartOfKey
	public final String name;

	public DisplayedName(String uid, String name) {
		super(uid);
		this.name = name;
	}
}
