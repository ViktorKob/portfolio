package net.model.meta_data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Indexable {
	public final String selectorType;
	public final String path;
	public final String documentType;
	public final String documentField;

	public Indexable(String selectorType, String path, String documentType, String documentField) {
		this.selectorType = selectorType;
		this.path = path;
		this.documentType = documentType;
		this.documentField = documentField;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
