package net.thomas.portfolio.hbase_index.schema.processing;

import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.isPrimitiveClass;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import net.thomas.portfolio.hbase_index.schema.annotations.IndexablePath;
import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.annotations.SchemaIgnore;
import net.thomas.portfolio.hbase_index.schema.annotations.SimpleRepresentable;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.selectors.SelectorEntity;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.FieldBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.FieldsBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveFieldBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.ReferenceFieldBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaBuilder;

public class SchemaIntrospection {
	private final HbaseIndexSchemaBuilder builder;
	private final Set<String> handledFieldTypes;
	private final Set<Class<?>> handledSimpleRepresentationTypes;

	public SchemaIntrospection() {
		builder = new HbaseIndexSchemaBuilder();
		handledFieldTypes = new HashSet<>();
		handledSimpleRepresentationTypes = new HashSet<>();
	}

	@SafeVarargs
	public final SchemaIntrospection examine(final Class<? extends Event>... entityClasses) {
		for (final Class<? extends Event> entityClass : entityClasses) {
			handledFieldTypes.add(entityClass.getSimpleName());
			examineEntityTypes(entityClass);
			examineFields(entityClass);
			examineSubTypes(entityClass);
			examineIndexableRelations(entityClass);
		}
		return this;
	}

	private SchemaIntrospection examineSubElement(final Class<?> entityClass) {
		handledFieldTypes.add(entityClass.getSimpleName());
		examineSubTypes(entityClass);
		examineFields(entityClass);
		examineEntityTypes(entityClass);
		return this;
	}

	private void examineEntityTypes(final Class<?> entityClass) {
		final String simpleName = entityClass.getSimpleName();
		if (Event.class.isAssignableFrom(entityClass)) {
			builder.addDocumentTypes(simpleName);
		} else if (isSelector(entityClass)) {
			builder.addSelectorTypes(simpleName);
			if (isSimpleRepresentable(entityClass)) {
				if (!handledSimpleRepresentationTypes.contains(entityClass)) {
					builder.addSimpleRepresentableTypes(simpleName);
					final SimpleRepresentable description = entityClass.getAnnotation(SimpleRepresentable.class);
					builder.addSimpleRepresentationParser(simpleName, description.field(), description.parser());
				}
			}
		}
	}

	private boolean isSimpleRepresentable(final Class<?> entityClass) {
		return entityClass.isAnnotationPresent(SimpleRepresentable.class);
	}

	private void examineFields(final Class<?> entityClass) {
		FieldsBuilder builder = new FieldsBuilder();
		for (final Field field : entityClass.getFields()) {
			if (!isIgnoredField(field)) {
				builder = addField(builder, field);
			}
		}
		this.builder.addFields(entityClass.getSimpleName(), builder.build());
	}

	private FieldsBuilder addField(final FieldsBuilder builder, final Field field) {
		final Class<?> fieldType = field.getType();
		FieldBuilder<?> fieldBuilder;
		if (isPrimitiveClass(fieldType)) {
			fieldBuilder = new PrimitiveFieldBuilder(field.getName()).setType(fieldType);
		} else {
			fieldBuilder = new ReferenceFieldBuilder(field.getName()).setType(getSubTypeName(fieldType));
		}
		if (isArray(field)) {
			fieldBuilder.markIsArray();
		}
		if (isKeyField(field)) {
			fieldBuilder.markIsPartOfKey();
		}
		builder.add(fieldBuilder.build());
		return builder;
	}

	private String getSubTypeName(Class<?> fieldType) {
		if (fieldType.isArray()) {
			fieldType = fieldType.getComponentType();
		}
		return fieldType.getSimpleName();
	}

	private void examineSubTypes(final Class<?> entityClass) {
		for (final Field field : entityClass.getFields()) {
			final Class<?> fieldType = field.getType();
			final String subTypeName = getSubTypeName(fieldType);
			if (!isPrimitiveClass(fieldType) && !handledFieldTypes.contains(subTypeName)) {
				if (fieldType.isArray()) {
					examineSubElement(fieldType.getComponentType());
				} else {
					examineSubElement(fieldType);
				}
			}
		}
	}

	private void examineIndexableRelations(final Class<? extends Event> document) {
		for (final Field field : document.getFields()) {
			final IndexablePath indexableDescription = field.getAnnotation(IndexablePath.class);
			if (indexableDescription != null) {
				final Set<Class<? extends Selector>> selectors = extractSelectors(field.getType(), new HashSet<>());
				for (final Class<? extends Selector> selector : selectors) {
					builder.addIndexable(selector.getSimpleName(), indexableDescription.value(), document.getSimpleName(), field.getName());
				}
			}
		}
	}

	@SuppressWarnings("unchecked") // Not true, we check it just before setting it
	private Set<Class<? extends Selector>> extractSelectors(Class<?> type, final Set<Class<?>> handledTypes) {
		if (type.isArray()) {
			type = type.getComponentType();
		}
		handledTypes.add(type);
		final Set<Class<? extends Selector>> selectors = new HashSet<>();
		if (isSelector(type)) {
			selectors.add((Class<? extends Selector>) type);
		}
		for (final Field field : type.getFields()) {
			final Class<?> subType = field.getType();
			if (!isPrimitiveClass(subType) && !handledTypes.contains(subType)) {
				selectors.addAll(extractSelectors(subType, handledTypes));
			}
		}
		return selectors;
	}

	private boolean isIgnoredField(final Field field) {
		return field.isAnnotationPresent(SchemaIgnore.class);
	}

	private boolean isSelector(final Class<?> entityClass) {
		return SelectorEntity.class.isAssignableFrom(entityClass);
	}

	private boolean isArray(final Field field) {
		return field.getType().isArray();
	}

	private boolean isKeyField(final Field field) {
		return field.getAnnotation(PartOfKey.class) != null;
	}

	public HbaseIndexSchema describeSchema() {
		return builder.build();
	}
}