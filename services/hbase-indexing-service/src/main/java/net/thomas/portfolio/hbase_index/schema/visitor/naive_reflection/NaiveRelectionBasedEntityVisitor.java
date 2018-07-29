package net.thomas.portfolio.hbase_index.schema.visitor.naive_reflection;

import java.lang.reflect.Field;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityVisitor;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

public class NaiveRelectionBasedEntityVisitor<CONTEXT_TYPE extends VisitingContext> implements EntityVisitor<CONTEXT_TYPE> {
	@SuppressWarnings("unused")
	private Object object;
	private final VisitorEntityPreAction<Entity, CONTEXT_TYPE> entityPreAction;
	private final VisitorEntityPostAction<Entity, CONTEXT_TYPE> entityPostAction;
	private final VisitorFieldPreAction<Entity, CONTEXT_TYPE> fieldPreAction;
	private final VisitorFieldPostAction<Entity, CONTEXT_TYPE> fieldPostAction;
	private final VisitorFieldSimpleAction<Entity, CONTEXT_TYPE> fieldSimpleAction;

	public NaiveRelectionBasedEntityVisitor(VisitorEntityPreAction<Entity, CONTEXT_TYPE> entityPreAction,
			VisitorEntityPostAction<Entity, CONTEXT_TYPE> entityPostAction, VisitorFieldPreAction<Entity, CONTEXT_TYPE> fieldPreAction,
			VisitorFieldPostAction<Entity, CONTEXT_TYPE> fieldPostAction, VisitorFieldSimpleAction<Entity, CONTEXT_TYPE> fieldSimpleAction) {
		this.entityPreAction = entityPreAction;
		this.entityPostAction = entityPostAction;
		this.fieldPreAction = fieldPreAction;
		this.fieldPostAction = fieldPostAction;
		this.fieldSimpleAction = fieldSimpleAction;
	}

	@Override
	public void visit(Entity entity, CONTEXT_TYPE context) {
		try {
			final Class<? extends Entity> entityClass = entity.getClass();
			entityPreAction.performEntityPreAction(entity, context);
			for (final Field field : entityClass.getFields()) {
				if ("uid".equals(field.getName())) {
					continue;
				} else if (field.getType()
					.isArray()) {
					fieldPreAction.performFieldPreAction(entity, context);
					final Entity[] subEntities = (Entity[]) field.get(entity);
					for (final Entity subEntity : subEntities) {
						visit(subEntity, context);
					}
					fieldPostAction.performFieldPostAction(entity, context);

				} else if (Entity.class.isAssignableFrom(field.getType())) {
					final Entity subEntity = (Entity) field.get(entity);
					if (subEntity != null) {
						fieldPreAction.performFieldPreAction(entity, context);
						visit(subEntity, context);
						fieldPostAction.performFieldPostAction(entity, context);
					}
				} else {
					object = field.get(entity);
					if (object != null) {
						fieldSimpleAction.performSimpleFieldAction(entity, context);
					}
				}
			}
			entityPostAction.performEntityPostAction(entity, context);
		} catch (IllegalAccessException | SecurityException e) {
			throw new RuntimeException("Unable to visit entity " + entity);
		}
	}
}