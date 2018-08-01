package net.thomas.portfolio.hbase_index.schema.selectors;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.annotations.SimpleRepresentable;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.EmailAddressSimpleRepParser;

@SimpleRepresentable(parser = EmailAddressSimpleRepParser.class)
public class EmailAddress extends SelectorEntity {
	@PartOfKey
	public final Localname localname;
	@PartOfKey
	public final Domain domain;

	public EmailAddress(Localname localname, Domain domain) {
		this.localname = localname;
		this.domain = domain;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (domain == null ? 0 : domain.hashCode());
		result = prime * result + (localname == null ? 0 : localname.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EmailAddress other = (EmailAddress) obj;
		if (domain == null) {
			if (other.domain != null) {
				return false;
			}
		} else if (!domain.equals(other.domain)) {
			return false;
		}
		if (localname == null) {
			if (other.localname != null) {
				return false;
			}
		} else if (!localname.equals(other.localname)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}
