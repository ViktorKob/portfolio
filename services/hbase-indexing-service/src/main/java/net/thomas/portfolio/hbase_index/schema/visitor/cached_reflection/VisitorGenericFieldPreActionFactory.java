package net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericFieldPreActionFactory<CONTEXT_TYPE extends VisitingContext> {
	VisitorGenericFieldPreAction<CONTEXT_TYPE> getGenericFieldPreAction(Class<? extends Entity> entityClass, String field);
}