package net.thomas.portfolio.hbase_index.schema.selectors;

import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.annotations.SimpleRepresentable;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.DomainSimpleRepParser;

@SimpleRepresentable(parser = DomainSimpleRepParser.class)
public class Domain extends SelectorEntity {
	@PartOfKey
	public final String domainPart;
	@PartOfKey
	public final Domain domain;

	public Domain(String uid, String domainPart, Domain domain) {
		super(uid);
		this.domainPart = domainPart;
		this.domain = domain;
	}
}