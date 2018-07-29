package net.thomas.portfolio.hbase_index.schema.visitor;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitorFieldSimpleAction;

@FunctionalInterface
public interface VisitorFieldSimpleActionFactory<CONTEXT_TYPE extends VisitingContext> {
	<T extends Entity> VisitorFieldSimpleAction<T, CONTEXT_TYPE> getFieldAction(Class<T> entityClass, String field);
}