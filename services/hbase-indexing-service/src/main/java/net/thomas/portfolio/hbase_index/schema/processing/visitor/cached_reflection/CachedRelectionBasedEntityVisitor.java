package net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection;

import java.lang.reflect.Field;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.EntityTraversalException;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.EntityVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.EntityActions;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.FieldActions;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

/***
 * Cached implementation of a reflection based depth-first entity traversal algorithm.
 *
 * Caches the structure of each type during construction and then leverage this information, when
 * executing the visit.
 */
@ThreadSafe
public class CachedRelectionBasedEntityVisitor<CONTEXT_TYPE extends VisitingContext> implements EntityVisitor<CONTEXT_TYPE> {
	private final Map<Object, EntityActions<CONTEXT_TYPE>> actionMap;

	public CachedRelectionBasedEntityVisitor(Map<Object, EntityActions<CONTEXT_TYPE>> actionMap) {
		this.actionMap = actionMap;
	}

	@Override
	public void visit(Entity entity, CONTEXT_TYPE context) {
		try {
			final Class<? extends Entity> entityClass = entity.getClass();
			final EntityActions<CONTEXT_TYPE> actions = actionMap.get(entityClass);
			actions.preEntity.performEntityPreAction(entity, context);
			for (final Field field : entityClass.getFields()) {
				if ("uid".equals(field.getName())) {
					continue;
				} else if (field.getType().isArray()) {
					final FieldActions<CONTEXT_TYPE> fieldActions = actions.fieldActions.get(field.getName());
					fieldActions.preField.performFieldPreAction(entity, context);
					final Entity[] subEntities = (Entity[]) field.get(entity);
					for (final Entity subEntity : subEntities) {
						visit(subEntity, context);
					}
					fieldActions.postField.performFieldPostAction(entity, context);

				} else if (Entity.class.isAssignableFrom(field.getType())) {
					final FieldActions<CONTEXT_TYPE> fieldActions = actions.fieldActions.get(field.getName());
					final Entity subEntity = (Entity) field.get(entity);
					if (subEntity != null) {
						fieldActions.preField.performFieldPreAction(entity, context);
						visit(subEntity, context);
						fieldActions.postField.performFieldPostAction(entity, context);
					}
				} else {
					final FieldActions<CONTEXT_TYPE> fieldActions = actions.fieldActions.get(field.getName());
					final Object object = field.get(entity);
					if (object != null) {
						fieldActions.simpleField.performSimpleFieldAction(entity, context);
					}
				}
			}
			actions.postEntity.performEntityPostAction(entity, context);
		} catch (IllegalAccessException | SecurityException cause) {
			throw new EntityTraversalException("Unable to visit entity " + entity, cause);
		}
	}
}