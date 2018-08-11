package net.thomas.portfolio.shared_objects.hbase_index.model.fields;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.FieldType.PRIMITIVE;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.DECIMAL;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.GEO_LOCATION;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.INTEGER;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.STRING;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.TIMESTAMP;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = PrimitiveField.class)
public class PrimitiveField implements Field {
	public static enum PrimitiveType {
		STRING(String.class),
		INTEGER(Integer.class, Long.class, Short.class, Byte.class),
		DECIMAL(Double.class, Float.class),
		TIMESTAMP(Timestamp.class),
		GEO_LOCATION(GeoLocation.class);

		public final Class<?>[] matchingClasses;

		private PrimitiveType(Class<?>... matchingClasses) {
			this.matchingClasses = matchingClasses;
		}
	}

	private static Map<Class<?>, PrimitiveType> CLASS_2_PRIMITIVE_TYPE;
	static {
		CLASS_2_PRIMITIVE_TYPE = new HashMap<>();
		for (final PrimitiveType type : PrimitiveType.values()) {
			for (final Class<?> matchingClass : type.matchingClasses) {
				CLASS_2_PRIMITIVE_TYPE.put(matchingClass, type);
			}
		}
	}

	public static boolean isPrimitiveClass(Class<?> candidateClass) {
		return CLASS_2_PRIMITIVE_TYPE.containsKey(candidateClass);
	}

	private String name;
	private PrimitiveType type;
	private boolean isArray;
	private boolean isKeyComponent;

	public PrimitiveField() {
	}

	public static PrimitiveField string(String name) {
		return new PrimitiveField(name, STRING, false, true);
	}

	public static PrimitiveField strings(String name) {
		return new PrimitiveField(name, STRING, true, true);
	}

	public static PrimitiveField integer(String name) {
		return new PrimitiveField(name, INTEGER, false, true);
	}

	public static PrimitiveField decimal(String name) {
		return new PrimitiveField(name, DECIMAL, false, true);
	}

	public static PrimitiveField timestamp(String name) {
		return new PrimitiveField(name, TIMESTAMP, false, true);
	}

	public static PrimitiveField geoLocation(String name) {
		return new PrimitiveField(name, GEO_LOCATION, false, true);
	}

	public static PrimitiveField nonKeyString(String name) {
		return new PrimitiveField(name, STRING, false, false);
	}

	public static PrimitiveField nonKeyInteger(String name) {
		return new PrimitiveField(name, INTEGER, false, false);
	}

	public static PrimitiveField nonKeyDecimal(String name) {
		return new PrimitiveField(name, DECIMAL, false, false);
	}

	public static PrimitiveField nonKeyTimestamp(String name) {
		return new PrimitiveField(name, TIMESTAMP, false, false);
	}

	public static PrimitiveField nonKeyGeoLocation(String name) {
		return new PrimitiveField(name, GEO_LOCATION, false, false);
	}

	public static PrimitiveField createNonKeyField(String name, PrimitiveType type) {
		return new PrimitiveField(name, type, false, false);
	}

	public static PrimitiveField createNonKeyArrayField(String name, PrimitiveType type) {
		return new PrimitiveField(name, type, true, false);
	}

	public static PrimitiveField createArrayField(String name, PrimitiveType type) {
		return new PrimitiveField(name, type, true, true);
	}

	public static class PrimitiveFieldBuilder extends FieldBuilder<PrimitiveField> {
		private PrimitiveType type;

		public PrimitiveFieldBuilder(String name) {
			super(name);
		}

		public PrimitiveFieldBuilder setType(Class<?> originalClass) {
			type = CLASS_2_PRIMITIVE_TYPE.get(originalClass);
			return this;
		}

		@Override
		public PrimitiveField build() {
			return new PrimitiveField(name, type, isArray, isPartOfKey);
		}
	}

	public PrimitiveField(String name, PrimitiveType type, Boolean isArray, Boolean isKeyComponent) {
		this.name = name;
		this.type = type;
		this.isArray = isArray != null ? isArray : false;
		this.isKeyComponent = isKeyComponent != null ? isKeyComponent : false;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(PrimitiveType type) {
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
		return PRIMITIVE;
	}

	@Override
	public String getName() {
		return name;
	}

	public PrimitiveType getType() {
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
		if (!(obj instanceof PrimitiveField)) {
			return false;
		}
		final PrimitiveField other = (PrimitiveField) obj;
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
		if (type != other.type) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}