package net.thomas.portfolio.hbase_index.schema.processing;

import static java.lang.String.valueOf;

import java.lang.reflect.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.thomas.portfolio.common.utils.ToStringUtil;
import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.Hasher;
import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.UidConverter;

public class UidCalculator {
	private final boolean keyShouldBeUnique;
	@JsonIgnore
	private final int counter;
	private final UidConverter uidConverter;

	public UidCalculator(boolean keyShouldBeUnique) {
		this.keyShouldBeUnique = keyShouldBeUnique;
		uidConverter = new UidConverter();
		counter = 0;
	}

	public String calculate(Entity entity) {
		final Hasher hasher = new Hasher();
		if (keyShouldBeUnique) {
			hasher.addUniqueness();
		}
		hasher.add(getSimpleNameAsBytes(entity));
		handleFields(hasher, entity);
		return hasher.digest();
	}

	private byte[] getSimpleNameAsBytes(Entity entity) {
		return entity.getClass()
			.getSimpleName()
			.getBytes();
	}

	private void handleFields(final Hasher hasher, Entity entity) {
		try {
			final Class<? extends Entity> entityClass = entity.getClass();
			for (final java.lang.reflect.Field field : entityClass.getFields()) {
				if (field.isAnnotationPresent(PartOfKey.class)) {
					addField(hasher, entity, field);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Unable to determine Uid", e);
		}
	}

	private void addField(final Hasher hasher, Entity entity, final Field field) throws IllegalAccessException {
		if (field.getType()
			.isArray()) {
			for (final Object listEntity : (Object[]) field.get(entity)) {
				hasher.add(interpret(field.get(listEntity)));
			}
		} else {
			hasher.add(interpret(field.get(entity)));
		}
	}

	private byte[] interpret(final Object value) {
		if (value instanceof Timestamp) {
			return valueOf(((Timestamp) value).getTimestamp()).getBytes();
		} else if (value instanceof Entity) {
			final String uid = ((Entity) value).uid;
			return uidConverter.convert(uid);
		} else {
			return valueOf(value).getBytes();
		}
	}

	@Override
	public String toString() {
		return ToStringUtil.asString(this);
	}
}
