package net.thomas.portfolio.shared_objects.hbase_index.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

//@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "dataType")
@JsonSerialize(using = DataTypeSerializer.class)
@JsonDeserialize(using = DataTypeDeserializer.class)
@JsonSubTypes({ @Type(value = Selector.class), @Type(value = Document.class), @Type(value = RawDataType.class) })
public class DataType {
	@JsonProperty
	protected DataTypeId id;
	protected Map<String, Object> fields;

	public DataType() {
		fields = new LinkedHashMap<>();
	}

	public DataType(DataTypeId id, Map<String, Object> fields) {
		this.id = id;
		this.fields = fields;
	}

	@JsonIgnore
	public void setUid(String uid) {
		id.setUid(uid);
	}

	public void setId(DataTypeId id) {
		this.id = id;
	}

	public DataTypeId getId() {
		return id;
	}

	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}

	@JsonSerialize(using = FieldsSerializer.class)
	public Map<String, Object> getFields() {
		return fields;
	}

	@JsonIgnore
	public void put(String field, Object value) {
		fields.put(field, value);
	}

	@JsonIgnore
	public boolean containsKey(String field) {
		return fields.containsKey(field);
	}

	@JsonIgnore
	public Object get(String field) {
		return fields.get(field);
	}

	@JsonIgnore
	public String getInRawForm() {
		return toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataType) {
			final DataType other = (DataType) obj;
			return id.equals(other.id) && fields.equals(other.fields);
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}