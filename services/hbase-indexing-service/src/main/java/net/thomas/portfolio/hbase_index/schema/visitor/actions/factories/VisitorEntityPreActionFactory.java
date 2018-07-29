package net.thomas.portfolio.hbase_index.schema.visitor.actions.factories;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorEntityPreActionFactory<CONTEXT_TYPE extends VisitingContext> {
	<T extends Entity> VisitorEntityPreAction<T, CONTEXT_TYPE> getEntityPreAction(Class<T> entityClass);
}