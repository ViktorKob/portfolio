package net.thomas.portfolio.shared_objects.hbase_index.model.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

public class UidGenerator {
	private final Collection<Field> fields;
	private final boolean keyShouldBeUnique;
	private final int counter;

	public UidGenerator(Collection<Field> fields, boolean keyShouldBeUnique) {
		this.fields = fields;
		this.keyShouldBeUnique = keyShouldBeUnique;
		counter = 0;
	}

	public synchronized String calculateUid(DataType entity) {
		try {
			final MessageDigest hasher = MessageDigest.getInstance("MD5");
			if (keyShouldBeUnique) {
				hasher.digest(String.valueOf(counter).getBytes());
			}

			if (entity instanceof Document) {
				hasher.update(String.valueOf(((Document) entity).getTimeOfEvent()).getBytes());
			}

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
			return uid;
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private void addField(final MessageDigest hasher, final Field field, final Object value) {
		if (field instanceof PrimitiveField) {
			hasher.update(value.toString().getBytes());
		} else if (field instanceof ReferenceField) {
			final DataType reference = (DataType) value;
			hasher.update(reference.getUid().getBytes());
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
