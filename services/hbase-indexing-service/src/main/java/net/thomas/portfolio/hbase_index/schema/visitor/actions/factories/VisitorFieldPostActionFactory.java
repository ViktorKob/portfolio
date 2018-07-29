package net.thomas.portfolio.hbase_index.schema.visitor.actions.factories;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorFieldPostActionFactory<CONTEXT_TYPE extends VisitingContext> {
	<T extends Entity> VisitorFieldPostAction<T, CONTEXT_TYPE> getFieldPostAction(Class<T> entityClass, String field);
}