package net.thomas.portfolio.hbase_index.schema.selectors;

import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.annotations.SimpleRepresentable;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.PositiveIntegerFieldSimpleRepParser;

@SimpleRepresentable(parser = PositiveIntegerFieldSimpleRepParser.class, field = "number")
public class PrivateId extends SelectorEntity {
	@PartOfKey
	public final String number;

	public PrivateId(String number) {
		this.number = number;
	}
}