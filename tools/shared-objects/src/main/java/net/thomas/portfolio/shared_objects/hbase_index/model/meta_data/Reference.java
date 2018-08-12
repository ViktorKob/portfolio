package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.Set;

public class Reference {
	private Source source;
	private String originalId;
	private Set<Classification> classifications;

	public Reference() {
	}

	public Reference(Source source, String originalId, Set<Classification> classifications) {
		this.source = source;
		this.originalId = originalId;
		this.classifications = classifications;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Set<Classification> getClassifications() {
		return classifications;
	}

	public void setClassifications(Set<Classification> classifications) {
		this.classifications = classifications;
	}

	public String getOriginalId() {
		return originalId;
	}

	public void setOriginalId(String originalId) {
		this.originalId = originalId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (classifications == null ? 0 : classifications.hashCode());
		result = prime * result + (originalId == null ? 0 : originalId.hashCode());
		result = prime * result + (source == null ? 0 : source.hashCode());
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
		if (!(obj instanceof Reference)) {
			return false;
		}
		final Reference other = (Reference) obj;
		if (classifications == null) {
			if (other.classifications != null) {
				return false;
			}
		} else if (!classifications.equals(other.classifications)) {
			return false;
		}
		if (originalId == null) {
			if (other.originalId != null) {
				return false;
			}
		} else if (!originalId.equals(other.originalId)) {
			return false;
		}
		if (source != other.source) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}