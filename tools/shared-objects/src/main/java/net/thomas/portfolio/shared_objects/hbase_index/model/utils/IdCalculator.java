package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

public class IdCalculator {
	private final Fields fields;
	private final boolean keyShouldBeUnique;
	@JsonIgnore
	private int counter;

	public IdCalculator(Fields fields, boolean keyShouldBeUnique) {
		this.fields = fields;
		this.keyShouldBeUnique = keyShouldBeUnique;
		counter = 0;
	}

	public synchronized DataTypeId calculate(String type, DataType entity) {
		try {
			final MessageDigest hasher = MessageDigest.getInstance("MD5");
			if (keyShouldBeUnique) {
				hasher.update(String.valueOf(counter++)
					.getBytes());
			}

			if (entity instanceof Document) {
				hasher.update(String.valueOf(((Document) entity).getTimeOfEvent())
					.getBytes());
			}

			hasher.update(type.getBytes());
			for (final Field field : fields) {
				if (field.isKeyComponent()) {
					final Object value = entity.get(field.getName());
					if (value != null) {
						if (field.isArray()) {
							for (final Object listEntity : (List<?>) value) {
								addField(hasher, field, listEntity);
							}
						} else {
							addField(hasher, field, value);
						}
					}
				}
			}
			final byte[] digest = hasher.digest();
			final String uid = DatatypeConverter.printHexBinary(digest);
			return new DataTypeId(type, uid);
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private void addField(final MessageDigest hasher, final Field field, final Object value) {
		if (field instanceof PrimitiveField) {
			hasher.update(value.toString()
				.getBytes());
		} else if (field instanceof ReferenceField) {
			final DataTypeId id = ((DataType) value).getId();
			hasher.update(id.uid.getBytes());
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
