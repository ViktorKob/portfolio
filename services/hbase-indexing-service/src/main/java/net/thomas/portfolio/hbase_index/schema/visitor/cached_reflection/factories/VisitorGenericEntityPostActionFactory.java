package net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericEntityPostActionFactory<CONTEXT_TYPE extends VisitingContext> {
	VisitorGenericEntityPostAction<CONTEXT_TYPE> getGenericEntityPostAction(Class<? extends Entity> entityClass);
}