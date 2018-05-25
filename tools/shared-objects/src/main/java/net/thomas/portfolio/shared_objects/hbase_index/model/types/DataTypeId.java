package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataTypeId {

	@JsonProperty
	private final String type;
	@JsonProperty
	private String uid;

	@JsonCreator
	public DataTypeId(@JsonProperty("type") String type, @JsonProperty("uid") String uid) {
		this.type = type;
		this.uid = uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getType() {
		return type;
	}

	public String getUid() {
		return uid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataTypeId) {
			final DataTypeId other = (DataTypeId) obj;
			return type.equals(other.type) && uid.equals(other.uid);
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}