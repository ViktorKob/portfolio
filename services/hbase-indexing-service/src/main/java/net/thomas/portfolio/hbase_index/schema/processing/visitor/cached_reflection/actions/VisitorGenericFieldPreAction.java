package net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericFieldPreAction<CONTEXT_TYPE extends VisitingContext> extends VisitorGenericFieldAction<CONTEXT_TYPE> {
	void performFieldPreAction(Entity entity, CONTEXT_TYPE context);
}