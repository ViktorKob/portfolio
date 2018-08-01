package net.thomas.portfolio.hbase_index.schema.selectors;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.annotations.SimpleRepresentable;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.DomainSimpleRepParser;

@SimpleRepresentable(parser = DomainSimpleRepParser.class)
public class Domain extends SelectorEntity {
	@PartOfKey
	public final String domainPart;
	@PartOfKey
	public final Domain domain;

	public Domain(String domainPart, Domain domain) {
		this.domainPart = domainPart;
		this.domain = domain;
	}

	public Domain(String domainPart) {
		this.domainPart = domainPart;
		domain = null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (domain == null ? 0 : domain.hashCode());
		result = prime * result + (domainPart == null ? 0 : domainPart.hashCode());
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
		final Domain other = (Domain) obj;
		if (domain == null) {
			if (other.domain != null) {
				return false;
			}
		} else if (!domain.equals(other.domain)) {
			return false;
		}
		if (domainPart == null) {
			if (other.domainPart != null) {
				return false;
			}
		} else if (!domainPart.equals(other.domainPart)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}