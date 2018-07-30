package net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericFieldSimpleActionFactory<CONTEXT_TYPE extends VisitingContext> {
	VisitorGenericFieldSimpleAction<CONTEXT_TYPE> getGenericSimpleFieldAction(Class<? extends Entity> entityClass, String field);
}