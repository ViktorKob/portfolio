package net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericEntityPostAction<CONTEXT_TYPE extends VisitingContext> extends VisitorEntityAction<CONTEXT_TYPE> {
	void performEntityPostAction(Entity entity, CONTEXT_TYPE context);
}