package net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericFieldPostAction<CONTEXT_TYPE extends VisitingContext> extends VisitorGenericFieldAction<CONTEXT_TYPE> {
	void performFieldPostAction(Entity entity, CONTEXT_TYPE context);
}