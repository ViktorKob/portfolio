package net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericEntityPreAction<CONTEXT_TYPE extends VisitingContext> extends VisitorGenericEntityAction<CONTEXT_TYPE> {
	void performEntityPreAction(Entity entity, CONTEXT_TYPE context);
}