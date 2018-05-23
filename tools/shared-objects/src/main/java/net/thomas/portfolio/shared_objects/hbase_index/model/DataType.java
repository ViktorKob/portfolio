package net.thomas.portfolio.shared_objects.hbase_index.model;

import java.util.HashMap;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DataType extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public DataType() {
		super();
	}

	public DataType(String type) {
		super();
		put("type", type);
	}

	public void setUid(String uid) {
		put("uid", uid);
	}

	public String getUid() {
		return (String) get("uid");
	}

	public String getType() {
		return (String) get("type");
	}

	public String getInRawForm() {
		return toString();
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}