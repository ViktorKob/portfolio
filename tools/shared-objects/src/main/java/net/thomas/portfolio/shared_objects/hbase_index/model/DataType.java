package net.thomas.portfolio.shared_objects.hbase_index.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = DataTypeDeserializer.class)
public class DataType extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public DataType() {
		super();
	}

	public DataType(DataTypeType dataTypeType, String type) {
		this();
		setDataTypeType(dataTypeType);
		setType(type);
	}

	public void setDataTypeType(DataTypeType type) {
		put("dataTypeType", type.name());
	}

	public void setType(String type) {
		put("type", type);
	}

	public void setUid(String uid) {
		put("uid", uid);
	}

	public String getDataTypeType() {
		return (String) get("dataTypeType");
	}

	public String getType() {
		return (String) get("type");
	}

	public String getUid() {
		return (String) get("uid");
	}

	public String getInRawForm() {
		return toString();
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public static DataType from(Object dataType) {
		if (dataType instanceof Map) {
			final DataType instance = new DataType();
			for (final Entry<?, ?> entry : ((Map<?, ?>) dataType).entrySet()) {
				instance.put(entry.getKey()
					.toString(), entry.getValue());
			}
			return instance;
		} else {
			return null;
		}
	}
}