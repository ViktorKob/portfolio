package net.thomas.portfolio.hbase_index.schema.processing;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import javax.annotation.concurrent.ThreadSafe;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.meta.MetaEntity;
import net.thomas.portfolio.hbase_index.schema.processing.Entity2DataTypeConverter.ConversionContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.StrictEntityHierarchyVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.StrictEntityHierarchyVisitorBuilder;
import net.thomas.portfolio.hbase_index.schema.selectors.SelectorEntity;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

@ThreadSafe
public class Entity2DataTypeConverter implements VisitorEntityPreActionFactory<ConversionContext>, VisitorEntityPostActionFactory<ConversionContext>,
		VisitorFieldPreActionFactory<ConversionContext>, VisitorFieldPostActionFactory<ConversionContext>
{
	private final StrictEntityHierarchyVisitor<ConversionContext> converter;

	public Entity2DataTypeConverter() {
		converter = new StrictEntityHierarchyVisitorBuilder<ConversionContext>().setEntityPreActionFactory(this)
				.setEntityPostActionFactory(this)
				.setFieldPreActionFactory(this)
				.setFieldPostActionFactory(this)
				.build();
	}

	public DataType convert(final Entity entity) {
		if (entity != null) {
			final ConversionContext context = new ConversionContext();
			converter.visit(entity, context);
			return context.buildStack.peek().getDataType();
		} else {
			return null;
		}
	}

	class ConversionContext implements VisitingContext {
		public final Stack<StackLevel> buildStack;

		public ConversionContext() {
			buildStack = new Stack<>();
			buildStack.push(new SimpleStackLevel());
		}
	}

	interface StackLevel {
		void addDataType(DataType entity);

		void put(String field, Object value);

		DataType getDataType();
	}

	class SimpleStackLevel implements StackLevel {
		public DataType entity;

		@Override
		public void addDataType(final DataType entity) {
			this.entity = entity;
		}

		@Override
		public void put(final String field, final Object value) {
			entity.put(field, value);
		}

		@Override
		public DataType getDataType() {
			return entity;
		}
	}

	class ListStackLevel implements StackLevel {
		public Collection<DataType> entities;
		public DataType latestEntity;

		public ListStackLevel() {
			entities = new LinkedList<>();
		}

		@Override
		public void addDataType(final DataType entity) {
			entities.add(entity);
			latestEntity = entity;
		}

		@Override
		public void put(final String field, final Object value) {
			latestEntity.put(field, value);
		}

		@Override
		public DataType getDataType() {
			return latestEntity;
		}
	}

	@Override
	public <T extends Entity> VisitorEntityPreAction<T, ConversionContext> getEntityPreAction(final Class<T> entityClass) {
		final Set<Field> relevantFields = stream(entityClass.getFields())
				.filter(field -> !Entity.class.isAssignableFrom(field.getType()) || "uid".equals(field.getName()))
				.collect(toSet());
		if (Event.class.isAssignableFrom(entityClass)) {
			final Field timeOfEvent = getField(entityClass, "timeOfEvent");
			final Field timeOfInterception = getField(entityClass, "timeOfInterception");
			relevantFields.remove(timeOfEvent);
			relevantFields.remove(timeOfInterception);
			return (entity, context) -> {
				final StackLevel currentLevel = context.buildStack.peek();
				final Document dataTypeEntity = new Document(new DataTypeId(getType(entity), entity.uid));
				updateDocumentWithTimeFieldsFromEntity(dataTypeEntity, entity, timeOfEvent, timeOfInterception);
				currentLevel.addDataType(dataTypeEntity);
				updateWithRelevantFieldsFromEntity(dataTypeEntity, entity, relevantFields);
			};
		} else if (MetaEntity.class.isAssignableFrom(entityClass)) {
			return (entity, context) -> {
				final RawDataType dataTypeEntity = new RawDataType(new DataTypeId(getType(entity), entity.uid));
				final StackLevel currentLevel = context.buildStack.peek();
				currentLevel.addDataType(dataTypeEntity);
				updateWithRelevantFieldsFromEntity(dataTypeEntity, entity, relevantFields);
			};
		} else if (SelectorEntity.class.isAssignableFrom(entityClass)) {
			return (entity, context) -> {
				final Selector dataTypeEntity = new Selector(new DataTypeId(getType(entity), entity.uid));
				final StackLevel currentLevel = context.buildStack.peek();
				currentLevel.addDataType(dataTypeEntity);
				updateWithRelevantFieldsFromEntity(dataTypeEntity, entity, relevantFields);
			};
		} else {
			throw new RuntimeException("Unable to convert entity of type " + entityClass.getSimpleName());
		}
	}

	private <T extends Entity> Field getField(final Class<T> entityClass, final String fieldName) {
		try {
			return entityClass.getField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Unable to convert " + entityClass + " - " + fieldName, e);
		}
	}

	private <T extends Entity> void updateDocumentWithTimeFieldsFromEntity(final Document dataTypeEntity, final Entity entity, final Field timeOfEvent,
			final Field timeOfInterception) {
		try {
			dataTypeEntity.setTimeOfEvent((Timestamp) timeOfEvent.get(entity));
			dataTypeEntity.setTimeOfInterception((Timestamp) timeOfInterception.get(entity));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Unable to convert node " + entity, e);
		}
	}

	private <T extends Entity> void updateWithRelevantFieldsFromEntity(final DataType dataTypeEntity, final Entity entity, final Set<Field> relevantFields) {
		for (final Field entityField : relevantFields) {
			try {
				dataTypeEntity.put(entityField.getName(), entityField.get(entity));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException("Unable to convert node " + entity, e);
			}
		}
	}

	@Override
	public <T extends Entity> VisitorEntityPostAction<T, ConversionContext> getEntityPostAction(final Class<T> entityClass) {
		return (entity, context) -> {
		};
	}

	@Override
	public <T extends Entity> VisitorFieldPreAction<T, ConversionContext> getFieldPreAction(final Class<T> entityClass, final String field) {
		try {
			final boolean isArray = entityClass.getField(field).getType().isArray();
			if (isArray) {
				return (entity, context) -> {
					context.buildStack.push(new ListStackLevel());
				};
			} else {
				return (entity, context) -> {
					context.buildStack.push(new SimpleStackLevel());
				};
			}
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Unable to build conversion hiearachy node for " + entityClass.getSimpleName() + " - " + field, e);
		}
	}

	@Override
	public <T extends Entity> VisitorFieldPostAction<T, ConversionContext> getFieldPostAction(final Class<T> entityClass, final String field) {
		try {
			final boolean isArray = entityClass.getField(field).getType().isArray();
			if (isArray) {
				return (entity, context) -> {
					final ListStackLevel subLevel = (ListStackLevel) context.buildStack.pop();
					final StackLevel currentLevel = context.buildStack.peek();
					currentLevel.put(field, subLevel.entities);
				};
			} else {
				return (entity, context) -> {
					final SimpleStackLevel subLevel = (SimpleStackLevel) context.buildStack.pop();
					final StackLevel currentLevel = context.buildStack.peek();
					currentLevel.put(field, subLevel.entity);
				};
			}
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Unable to build conversion hiearachy node for " + entityClass.getSimpleName() + " - " + field, e);
		}
	}

	private <T extends Entity> String getType(final T entity) {
		return entity.getClass().getSimpleName();
	}
}
