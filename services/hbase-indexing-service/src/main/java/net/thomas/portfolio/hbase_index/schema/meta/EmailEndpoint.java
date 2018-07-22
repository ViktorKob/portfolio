package net.thomas.portfolio.hbase_index.schema.meta;

import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;

public class EmailEndpoint extends MetaEntity {
	@PartOfKey
	public final DisplayedName displayedName;
	@PartOfKey
	public final EmailAddress address;

	public EmailEndpoint(String uid, DisplayedName displayedName, EmailAddress address) {
		super(uid);
		this.displayedName = displayedName;
		this.address = address;
	}
}