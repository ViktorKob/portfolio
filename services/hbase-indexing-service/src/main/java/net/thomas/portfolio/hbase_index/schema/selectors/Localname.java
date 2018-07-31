package net.thomas.portfolio.hbase_index.schema.selectors;

import net.thomas.portfolio.annotations.CoverageIgnoredMethod;
import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.annotations.SimpleRepresentable;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.StringFieldSimpleRepParser;

@SimpleRepresentable(parser = StringFieldSimpleRepParser.class, field = "name")
public class Localname extends SelectorEntity {
	@PartOfKey
	public final String name;

	public Localname(String name) {
		this.name = name;
	}

	@Override
	@CoverageIgnoredMethod
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		return result;
	}

	@Override
	@CoverageIgnoredMethod
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
		final Localname other = (Localname) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	@CoverageIgnoredMethod
	public String toString() {
		return "Localname [name=" + name + "]";
	}
}