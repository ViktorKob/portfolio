package net.thomas.portfolio.hbase_index.schema.visitor;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitorFieldPreAction;

@FunctionalInterface
public interface VisitorFieldPreActionFactory<CONTEXT_TYPE extends VisitingContext> {
	<T extends Entity> VisitorFieldPreAction<T, CONTEXT_TYPE> getFieldPreAction(Class<T> entityClass, String field);
}