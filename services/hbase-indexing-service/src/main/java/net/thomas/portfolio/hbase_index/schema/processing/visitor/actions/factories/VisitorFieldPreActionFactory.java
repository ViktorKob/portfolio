package net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorFieldPreActionFactory<CONTEXT_TYPE extends VisitingContext> {
	<T extends Entity> VisitorFieldPreAction<T, CONTEXT_TYPE> getFieldPreAction(Class<T> entityClass, String field);
}