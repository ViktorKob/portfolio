package net.thomas.portfolio.hbase_index.schema.visitor;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitorFieldPostAction;

@FunctionalInterface
public interface VisitorFieldPostActionFactory<CONTEXT_TYPE extends VisitingContext> {
	<T extends Entity> VisitorFieldPostAction<T, CONTEXT_TYPE> getFieldPostAction(Class<T> entityClass, String field);
}