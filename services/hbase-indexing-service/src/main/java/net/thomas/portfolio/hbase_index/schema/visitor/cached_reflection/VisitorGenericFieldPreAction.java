package net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

@FunctionalInterface
public interface VisitorGenericFieldPreAction<CONTEXT_TYPE extends VisitingContext> extends VisitorFieldAction<CONTEXT_TYPE> {
	void performFieldPreAction(Entity entity, CONTEXT_TYPE context);
}