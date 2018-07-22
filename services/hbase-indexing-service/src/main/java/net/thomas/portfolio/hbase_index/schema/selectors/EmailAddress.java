package net.thomas.portfolio.hbase_index.schema.selectors;

import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.annotations.SimpleRepresentable;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.EmailAddressSimpleRepParser;

@SimpleRepresentable(parser = EmailAddressSimpleRepParser.class)
public class EmailAddress extends SelectorEntity {
	@PartOfKey
	public final Localname localname;
	@PartOfKey
	public final Domain domain;

	public EmailAddress(String uid, Localname localname, Domain domain) {
		super(uid);
		this.localname = localname;
		this.domain = domain;
	}
}
