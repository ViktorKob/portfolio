package net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericFieldSimpleAction<CONTEXT_TYPE extends VisitingContext> extends VisitorGenericFieldAction<CONTEXT_TYPE> {
	void performSimpleFieldAction(Entity entity, CONTEXT_TYPE context);
}