package net.thomas.portfolio.hbase_index.schema;

import static java.lang.String.valueOf;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class IdCalculator {
	private final Fields fields;
	private final boolean keyShouldBeUnique;

	public IdCalculator(Fields fields, boolean keyShouldBeUnique) {
		this.fields = fields;
		this.keyShouldBeUnique = keyShouldBeUnique;
	}

	public synchronized DataTypeId calculate(String type, DataType entity) {
		final Hasher hasher = new Hasher();
		if (keyShouldBeUnique) {
			hasher.addUniqueness();
		}

		hasher.add(type.getBytes());
		for (final Field field : fields) {
			if (field.isKeyComponent()) {
				final Object value = entity.get(field.getName());
				if (value != null) {
					if (field.isArray()) {
						for (final Object listEntity : (List<?>) value) {
							hasher.add(addField(field, listEntity));
						}
					} else {
						hasher.add(addField(field, value));
					}
				}
			}
		}

		if (entity instanceof Document) {
			hasher.add(String.valueOf(((Document) entity).getTimeOfEvent())
				.getBytes());
		}
		final String uid = hasher.digest();
		return new DataTypeId(type, uid);
	}

	private byte[] addField(final Field field, final Object value) {
		if (field instanceof PrimitiveField) {
			if (PrimitiveType.TIMESTAMP == ((PrimitiveField) field).getType()) {
				return valueOf(((Timestamp) value).getTimestamp()).getBytes();
			} else {
				return value.toString()
					.getBytes();
			}
		} else if (field instanceof ReferenceField) {
			final DataTypeId id = ((DataType) value).getId();
			return id.uid.getBytes();
		} else {
			throw new RuntimeException("Unknown field " + field);
		}
	}

	public Fields getFields() {
		return fields;
	}

	public boolean isKeyShouldBeUnique() {
		return keyShouldBeUnique;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
