package net.thomas.portfolio.shared_objects.hbase_index.model.fields;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.FieldType.REFERENCE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = ReferenceField.class)
public class ReferenceField implements Field {
	private String name;
	private String type;
	private boolean isArray;
	private boolean isKeyComponent;

	public ReferenceField() {
	}

	public static ReferenceField dataType(String name, String type) {
		return new ReferenceField(name, type, false, true);
	}

	public static ReferenceField nonKeyDataType(String name, String type) {
		return new ReferenceField(name, type, false, false);
	}

	public static ReferenceField dataTypeArray(String name, String type) {
		return new ReferenceField(name, type, true, true);
	}

	public static ReferenceField nonKeyDataTypeArray(String name, String type) {
		return new ReferenceField(name, type, true, false);
	}

	public static class ReferenceFieldBuilder extends FieldBuilder<ReferenceField> {
		private String type;

		public ReferenceFieldBuilder(String name) {
			super(name);
		}

		public ReferenceFieldBuilder setType(String type) {
			this.type = type;
			return this;
		}

		@Override
		public ReferenceField build() {
			return new ReferenceField(name, type, isArray, isPartOfKey);
		}
	}

	public ReferenceField(String name, String type, Boolean isArray, Boolean isKeyComponent) {
		this.name = name;
		this.type = type;
		this.isArray = isArray != null ? isArray : false;
		this.isKeyComponent = isKeyComponent != null ? isKeyComponent : false;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}

	public void setKeyComponent(boolean isKeyComponent) {
		this.isKeyComponent = isKeyComponent;
	}

	@Override
	public FieldType getFieldType() {
		return REFERENCE;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	@Override
	public boolean isArray() {
		return isArray;
	}

	@Override
	public boolean isKeyComponent() {
		return isKeyComponent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isArray ? 1231 : 1237);
		result = prime * result + (isKeyComponent ? 1231 : 1237);
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (type == null ? 0 : type.hashCode());
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
		if (!(obj instanceof ReferenceField)) {
			return false;
		}
		final ReferenceField other = (ReferenceField) obj;
		if (isArray != other.isArray) {
			return false;
		}
		if (isKeyComponent != other.isKeyComponent) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}