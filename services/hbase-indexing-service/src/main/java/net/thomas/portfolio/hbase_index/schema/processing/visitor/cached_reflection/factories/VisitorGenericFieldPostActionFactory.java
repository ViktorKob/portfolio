package net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.factories;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.VisitorGenericFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericFieldPostActionFactory<CONTEXT_TYPE extends VisitingContext> {
	VisitorGenericFieldPostAction<CONTEXT_TYPE> getGenericFieldPostAction(Class<? extends Entity> entityClass, String field);
}