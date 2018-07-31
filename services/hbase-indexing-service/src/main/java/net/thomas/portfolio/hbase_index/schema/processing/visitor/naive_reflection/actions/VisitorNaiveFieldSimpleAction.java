package net.thomas.portfolio.hbase_index.schema.processing.visitor.naive_reflection.actions;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorNaiveFieldSimpleAction<ENTITY_TYPE extends Entity, CONTEXT_TYPE extends VisitingContext>
		extends VisitorNaiveFieldAction<ENTITY_TYPE, CONTEXT_TYPE> {
	void performNaiveSimpleFieldAction(ENTITY_TYPE entity, CONTEXT_TYPE context, String field);
}