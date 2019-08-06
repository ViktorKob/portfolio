package net.thomas.portfolio.shared_objects.hbase_index.model.fields;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.thomas.portfolio.shared_objects.hbase_index.model.serializers.FieldDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = FieldDeserializer.class)
@ApiModel(description = "The definition for a specific field in a data type in the model")
public interface Field {
	@ApiModelProperty("Whether the field is a primitive value or a reference to another data type")
	FieldType getFieldType();

	@ApiModelProperty("Whether the field is considered when calculating the unique id for the data type")
	boolean isKeyComponent();

	@ApiModelProperty("The locally unique name of the field inside the data type")
	String getName();

	@ApiModelProperty("Whether the field supports multiple values simultanously")
	boolean isArray();
}
