package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

public class Indexable {
	public String selectorType;
	public String path;
	public String documentType;
	public String documentField;

	public Indexable() {
	}

	public Indexable(String selectorType, String path, String documentType, String documentField) {
		this.selectorType = selectorType;
		this.path = path;
		this.documentType = documentType;
		this.documentField = documentField;
	}

	public String getSelectorType() {
		return selectorType;
	}

	public void setSelectorType(String selectorType) {
		this.selectorType = selectorType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentField() {
		return documentField;
	}

	public void setDocumentField(String documentField) {
		this.documentField = documentField;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (documentField == null ? 0 : documentField.hashCode());
		result = prime * result + (documentType == null ? 0 : documentType.hashCode());
		result = prime * result + (path == null ? 0 : path.hashCode());
		result = prime * result + (selectorType == null ? 0 : selectorType.hashCode());
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
		if (!(obj instanceof Indexable)) {
			return false;
		}
		final Indexable other = (Indexable) obj;
		if (documentField == null) {
			if (other.documentField != null) {
				return false;
			}
		} else if (!documentField.equals(other.documentField)) {
			return false;
		}
		if (documentType == null) {
			if (other.documentType != null) {
				return false;
			}
		} else if (!documentType.equals(other.documentType)) {
			return false;
		}
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		if (selectorType == null) {
			if (other.selectorType != null) {
				return false;
			}
		} else if (!selectorType.equals(other.selectorType)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}