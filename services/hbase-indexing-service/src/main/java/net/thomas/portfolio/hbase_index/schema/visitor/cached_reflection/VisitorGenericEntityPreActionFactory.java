package net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericEntityPreActionFactory<CONTEXT_TYPE extends VisitingContext> {
	VisitorGenericEntityPreAction<CONTEXT_TYPE> getGenericEntityPreAction(Class<? extends Entity> entityClass);
}