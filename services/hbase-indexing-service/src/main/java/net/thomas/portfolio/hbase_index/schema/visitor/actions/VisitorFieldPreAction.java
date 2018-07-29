package net.thomas.portfolio.hbase_index.schema.visitor.actions;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorFieldPreAction<ENTITY_TYPE extends Entity, CONTEXT_TYPE extends VisitingContext>
		extends VisitorFieldAction<ENTITY_TYPE, CONTEXT_TYPE> {
	void performFieldPreAction(ENTITY_TYPE entity, CONTEXT_TYPE context);
}