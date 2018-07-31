package net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorFieldSimpleActionFactory<CONTEXT_TYPE extends VisitingContext> {
	<T extends Entity> VisitorFieldSimpleAction<T, CONTEXT_TYPE> getSimpleFieldAction(Class<T> entityClass, String field);
}