package net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericFieldPostAction<CONTEXT_TYPE extends VisitingContext> extends VisitorFieldAction<CONTEXT_TYPE> {
	void performFieldPostAction(Entity entity, CONTEXT_TYPE context);
}