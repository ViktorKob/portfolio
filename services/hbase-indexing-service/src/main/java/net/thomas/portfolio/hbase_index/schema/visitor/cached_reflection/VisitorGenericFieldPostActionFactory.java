package net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericFieldPostActionFactory<CONTEXT_TYPE extends VisitingContext> {
	VisitorGenericFieldPostAction<CONTEXT_TYPE> getGenericFieldPostAction(Class<? extends Entity> entityClass, String field);
}