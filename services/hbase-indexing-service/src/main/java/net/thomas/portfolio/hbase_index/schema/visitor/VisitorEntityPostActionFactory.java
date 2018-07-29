package net.thomas.portfolio.hbase_index.schema.visitor;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitorEntityPostAction;

@FunctionalInterface
public interface VisitorEntityPostActionFactory<CONTEXT_TYPE extends VisitingContext> {
	<T extends Entity> VisitorEntityPostAction<T, CONTEXT_TYPE> getEntityPostAction(Class<T> entityClass);
}