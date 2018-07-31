package net.thomas.portfolio.hbase_index.schema.processing.visitor.actions;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorFieldPostAction<ENTITY_TYPE extends Entity, CONTEXT_TYPE extends VisitingContext>
		extends VisitorFieldAction<ENTITY_TYPE, CONTEXT_TYPE> {
	void performFieldPostAction(ENTITY_TYPE entity, CONTEXT_TYPE context);
}