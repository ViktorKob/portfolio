package net.thomas.portfolio.hbase_index.schema.meta;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.selectors.PrivateId;
import net.thomas.portfolio.hbase_index.schema.selectors.PublicId;

public class CommunicationEndpoint extends MetaEntity {
	@PartOfKey
	public final PublicId publicId;
	@PartOfKey
	public final PrivateId privateId;

	public CommunicationEndpoint(PublicId publicId, PrivateId privateId) {
		this.publicId = publicId;
		this.privateId = privateId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (privateId == null ? 0 : privateId.hashCode());
		result = prime * result + (publicId == null ? 0 : publicId.hashCode());
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
		final CommunicationEndpoint other = (CommunicationEndpoint) obj;
		if (privateId == null) {
			if (other.privateId != null) {
				return false;
			}
		} else if (!privateId.equals(other.privateId)) {
			return false;
		}
		if (publicId == null) {
			if (other.publicId != null) {
				return false;
			}
		} else if (!publicId.equals(other.publicId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}