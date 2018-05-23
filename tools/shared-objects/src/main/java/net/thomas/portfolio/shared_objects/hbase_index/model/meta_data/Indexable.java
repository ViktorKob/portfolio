package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

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
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}