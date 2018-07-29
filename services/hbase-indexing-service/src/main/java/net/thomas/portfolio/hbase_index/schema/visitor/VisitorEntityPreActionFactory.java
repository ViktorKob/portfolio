package net.thomas.portfolio.hbase_index.schema.visitor;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitorEntityPreAction;

@FunctionalInterface
public interface VisitorEntityPreActionFactory<CONTEXT_TYPE extends VisitingContext> {
	<T extends Entity> VisitorEntityPreAction<T, CONTEXT_TYPE> getEntityPreAction(Class<T> entityClass);
}