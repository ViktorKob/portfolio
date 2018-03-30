package net.model;

import java.util.HashMap;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DataType extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;
	protected final String type;
	protected String uid;

	public DataType(String type) {
		super();
		this.type = type;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}

	public String getType() {
		return type;
	}

	public String getInRawForm() {
		return toString();
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}