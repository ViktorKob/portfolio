package net.thomas.portfolio.hbase_index.schema.meta;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;

public class EmailEndpoint extends MetaEntity {
	@PartOfKey
	public final DisplayedName displayedName;
	@PartOfKey
	public final EmailAddress address;

	public EmailEndpoint(DisplayedName displayedName, EmailAddress address) {
		this.displayedName = displayedName;
		this.address = address;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (address == null ? 0 : address.hashCode());
		result = prime * result + (displayedName == null ? 0 : displayedName.hashCode());
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
		final EmailEndpoint other = (EmailEndpoint) obj;
		if (address == null) {
			if (other.address != null) {
				return false;
			}
		} else if (!address.equals(other.address)) {
			return false;
		}
		if (displayedName == null) {
			if (other.displayedName != null) {
				return false;
			}
		} else if (!displayedName.equals(other.displayedName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}