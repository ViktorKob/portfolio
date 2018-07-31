package net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.EntityActions;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.FieldActions;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.VisitorGenericEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.VisitorGenericEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.VisitorGenericFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.VisitorGenericFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.VisitorGenericFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.factories.VisitorGenericEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.factories.VisitorGenericEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.factories.VisitorGenericFieldPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.factories.VisitorGenericFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.factories.VisitorGenericFieldSimpleActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

public class CachedReflectionBasedEntityVisitorBuilder<CONTEXT_TYPE extends VisitingContext> {
	private VisitorGenericEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory;
	private VisitorGenericEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory;
	private VisitorGenericFieldSimpleActionFactory<CONTEXT_TYPE> simpleFieldActionFactory;
	private VisitorGenericFieldPreActionFactory<CONTEXT_TYPE> preFieldActionFactory;
	private VisitorGenericFieldPostActionFactory<CONTEXT_TYPE> postFieldActionFactory;
	private final Set<Class<? extends Event>> eventClasses;

	public CachedReflectionBasedEntityVisitorBuilder(Set<Class<? extends Event>> eventClasses) {
		this.eventClasses = eventClasses;
		preEntityActionFactory = new VisitorGenericEntityPreActionFactory<CONTEXT_TYPE>() {
			@Override
			public VisitorGenericEntityPreAction<CONTEXT_TYPE> getGenericEntityPreAction(Class<? extends Entity> entityClass) {
				return (entity, context) -> {
				};
			}
		};
		postEntityActionFactory = new VisitorGenericEntityPostActionFactory<CONTEXT_TYPE>() {
			@Override
			public VisitorGenericEntityPostAction<CONTEXT_TYPE> getGenericEntityPostAction(Class<? extends Entity> entityClass) {
				return (entity, context) -> {
				};
			}
		};
		simpleFieldActionFactory = new VisitorGenericFieldSimpleActionFactory<CONTEXT_TYPE>() {
			@Override
			public VisitorGenericFieldSimpleAction<CONTEXT_TYPE> getGenericSimpleFieldAction(Class<? extends Entity> entityClass, String field) {
				return (entity, context) -> {
				};
			}
		};
		preFieldActionFactory = new VisitorGenericFieldPreActionFactory<CONTEXT_TYPE>() {
			@Override
			public VisitorGenericFieldPreAction<CONTEXT_TYPE> getGenericFieldPreAction(Class<? extends Entity> entityClass, String field) {
				return (entity, context) -> {
				};
			}
		};
		postFieldActionFactory = new VisitorGenericFieldPostActionFactory<CONTEXT_TYPE>() {
			@Override
			public VisitorGenericFieldPostAction<CONTEXT_TYPE> getGenericFieldPostAction(Class<? extends Entity> entityClass, String field) {
				return (entity, context) -> {
				};
			}
		};
	}

	public CachedReflectionBasedEntityVisitorBuilder<CONTEXT_TYPE> setEntityPreActionFactory(VisitorGenericEntityPreActionFactory<CONTEXT_TYPE> actionFactory) {
		preEntityActionFactory = actionFactory;
		return this;
	}

	public CachedReflectionBasedEntityVisitorBuilder<CONTEXT_TYPE> setEntityPostActionFactory(
			VisitorGenericEntityPostActionFactory<CONTEXT_TYPE> actionFactory) {
		postEntityActionFactory = actionFactory;
		return this;
	}

	public CachedReflectionBasedEntityVisitorBuilder<CONTEXT_TYPE> setFieldSimpleActionFactory(
			VisitorGenericFieldSimpleActionFactory<CONTEXT_TYPE> actionFactory) {
		simpleFieldActionFactory = actionFactory;
		return this;
	}

	public CachedReflectionBasedEntityVisitorBuilder<CONTEXT_TYPE> setFieldPreActionFactory(VisitorGenericFieldPreActionFactory<CONTEXT_TYPE> actionFactory) {
		preFieldActionFactory = actionFactory;
		return this;
	}

	public CachedReflectionBasedEntityVisitorBuilder<CONTEXT_TYPE> setFieldPostActionFactory(VisitorGenericFieldPostActionFactory<CONTEXT_TYPE> actionFactory) {
		postFieldActionFactory = actionFactory;
		return this;
	}

	public CachedRelectionBasedEntityVisitor<CONTEXT_TYPE> build() {
		final Map<Object, EntityActions<CONTEXT_TYPE>> actionMap = new HashMap<>();
		for (final Class<? extends Entity> eventClass : eventClasses) {
			preloadActionsFor(actionMap, eventClass);
		}
		return new CachedRelectionBasedEntityVisitor<>(actionMap);
	}

	@SuppressWarnings("unchecked")
	private void preloadActionsFor(final Map<Object, EntityActions<CONTEXT_TYPE>> actionMap, final Class<? extends Entity> entityClass) {
		final EntityActions<CONTEXT_TYPE> entityActions = createEntityActions(entityClass);
		actionMap.put(entityClass, entityActions);
		for (final Field field : entityClass.getFields()) {
			final Class<?> fieldType = field.getType();
			if (fieldType.isArray()) {
				if (Entity.class.isAssignableFrom(fieldType.getComponentType())) {
					if (!actionMap.containsKey(fieldType.getComponentType())) {
						preloadActionsFor(actionMap, (Class<? extends Entity>) fieldType.getComponentType());
					}
					entityActions.fieldActions.put(field.getName(),
							createFieldEntityActions((Class<? extends Entity>) fieldType.getComponentType(), field.getName()));
				}

			} else if (Entity.class.isAssignableFrom(fieldType)) {
				if (!actionMap.containsKey(fieldType)) {
					preloadActionsFor(actionMap, (Class<? extends Entity>) fieldType);
				}
				entityActions.fieldActions.put(field.getName(), createFieldEntityActions((Class<? extends Entity>) fieldType, field.getName()));
			} else {
				entityActions.fieldActions.put(field.getName(), createFieldSimpleActions((Class<? extends Entity>) fieldType, field.getName()));
			}
		}
	}

	private EntityActions<CONTEXT_TYPE> createEntityActions(Class<? extends Entity> eventClass) {
		return new EntityActions<>(preEntityActionFactory.getGenericEntityPreAction(eventClass), postEntityActionFactory.getGenericEntityPostAction(eventClass),
				new HashMap<>());
	}

	private FieldActions<CONTEXT_TYPE> createFieldEntityActions(Class<? extends Entity> entityClass, String field) {
		return new FieldActions<>(preFieldActionFactory.getGenericFieldPreAction(entityClass, field),
				postFieldActionFactory.getGenericFieldPostAction(entityClass, field), null);
	}

	private FieldActions<CONTEXT_TYPE> createFieldSimpleActions(Class<? extends Entity> entityClass, String field) {
		return new FieldActions<>(null, null, simpleFieldActionFactory.getGenericSimpleFieldAction(entityClass, field));
	}
}