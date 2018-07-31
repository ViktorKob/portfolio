package net.thomas.portfolio.hbase_index.schema.processing.visitor.actions;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorEntityPostAction<ENTITY_TYPE extends Entity, CONTEXT_TYPE extends VisitingContext>
		extends VisitorEntityAction<ENTITY_TYPE, CONTEXT_TYPE> {
	void performEntityPostAction(ENTITY_TYPE entity, CONTEXT_TYPE context);
}