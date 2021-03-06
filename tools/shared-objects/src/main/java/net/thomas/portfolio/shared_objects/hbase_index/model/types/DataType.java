package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.thomas.portfolio.shared_objects.hbase_index.model.serializers.DataTypeDeserializer;
import net.thomas.portfolio.shared_objects.hbase_index.model.serializers.DataTypeSerializer;
import net.thomas.portfolio.shared_objects.hbase_index.model.serializers.FieldsSerializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(using = DataTypeSerializer.class)
@JsonDeserialize(using = DataTypeDeserializer.class)
@JsonSubTypes({ @Type(Selector.class), @Type(Document.class), @Type(RawDataType.class) })
@ApiModel(description = "A specific entity of some type from the model")
public class DataType {
	@ApiModelProperty("The id for this specific entity in the model, guaranteed to be globally unique")
	protected DataTypeId id;
	@ApiModelProperty("The values for each field supported by this specific type {String: Field}")
	protected Map<String, Object> fields;

	public DataType() {
		this(null);
	}

	public DataType(DataTypeId id) {
		this(id, new LinkedHashMap<>());
	}

	public DataType(DataTypeId id, Map<String, Object> fields) {
		this.id = id;
		this.fields = fields;
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
	@SuppressWarnings("unchecked")
	public <T> T get(String field) {
		return (T) fields.get(field);
	}

	@JsonIgnore
	public String getInRawForm() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataType) {
			if (id == null) {
				return ((DataType) obj).id == null;
			} else {
				return id.equals(((DataType) obj).id);
			}
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return asString(this);
	}
}