package net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericFieldPreActionFactory<CONTEXT_TYPE extends VisitingContext> {
	VisitorGenericFieldPreAction<CONTEXT_TYPE> getGenericFieldPreAction(Class<? extends Entity> entityClass, String field);
}