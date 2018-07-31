package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

public interface EntityVisitor<CONTEXT_TYPE extends VisitingContext> {
	void visit(Entity entity, CONTEXT_TYPE context);
}