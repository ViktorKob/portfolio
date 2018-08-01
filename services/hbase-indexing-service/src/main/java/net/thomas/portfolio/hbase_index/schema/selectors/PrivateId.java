package net.thomas.portfolio.hbase_index.schema.selectors;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (number == null ? 0 : number.hashCode());
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
		final PrivateId other = (PrivateId) obj;
		if (number == null) {
			if (other.number != null) {
				return false;
			}
		} else if (!number.equals(other.number)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}