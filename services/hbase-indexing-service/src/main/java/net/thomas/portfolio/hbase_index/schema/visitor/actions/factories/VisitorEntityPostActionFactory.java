package net.thomas.portfolio.hbase_index.schema.visitor.actions.factories;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorEntityPostActionFactory<CONTEXT_TYPE extends VisitingContext> {
	<T extends Entity> VisitorEntityPostAction<T, CONTEXT_TYPE> getEntityPostAction(Class<T> entityClass);
}