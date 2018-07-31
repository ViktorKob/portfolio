package net.thomas.portfolio.hbase_index.schema.processing.visitor.naive_reflection;

import java.lang.reflect.Field;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.EntityVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.naive_reflection.actions.VisitorNaiveFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.naive_reflection.actions.VisitorNaiveFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.naive_reflection.actions.VisitorNaiveFieldSimpleAction;

public class NaiveRelectionBasedEntityVisitor<CONTEXT_TYPE extends VisitingContext> implements EntityVisitor<CONTEXT_TYPE> {
	private Object object;
	private final VisitorEntityPreAction<Entity, CONTEXT_TYPE> entityPreAction;
	private final VisitorEntityPostAction<Entity, CONTEXT_TYPE> entityPostAction;
	private final VisitorNaiveFieldPreAction<Entity, CONTEXT_TYPE> fieldPreAction;
	private final VisitorNaiveFieldPostAction<Entity, CONTEXT_TYPE> fieldPostAction;
	private final VisitorNaiveFieldSimpleAction<Entity, CONTEXT_TYPE> fieldSimpleAction;

	public NaiveRelectionBasedEntityVisitor(VisitorEntityPreAction<Entity, CONTEXT_TYPE> entityPreAction,
			VisitorEntityPostAction<Entity, CONTEXT_TYPE> entityPostAction, VisitorNaiveFieldPreAction<Entity, CONTEXT_TYPE> fieldPreAction,
			VisitorNaiveFieldPostAction<Entity, CONTEXT_TYPE> fieldPostAction, VisitorNaiveFieldSimpleAction<Entity, CONTEXT_TYPE> fieldSimpleAction) {
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
					fieldPreAction.performNaiveFieldPreAction(entity, context, field.getName());
					final Entity[] subEntities = (Entity[]) field.get(entity);
					for (final Entity subEntity : subEntities) {
						visit(subEntity, context);
					}
					fieldPostAction.performNaiveFieldPostAction(entity, context, field.getName());
				} else if (Entity.class.isAssignableFrom(field.getType())) {
					final Entity subEntity = (Entity) field.get(entity);
					if (subEntity != null) {
						fieldPreAction.performNaiveFieldPreAction(entity, context, field.getName());
						visit(subEntity, context);
						fieldPostAction.performNaiveFieldPostAction(entity, context, field.getName());
					}
				} else {
					object = field.get(entity);
					if (object != null) {
						fieldSimpleAction.performNaiveSimpleFieldAction(entity, context, field.getName());
					}
				}
			}
			entityPostAction.performEntityPostAction(entity, context);
		} catch (IllegalAccessException | SecurityException e) {
			throw new RuntimeException("Unable to visit entity " + entity);
		}
	}
}