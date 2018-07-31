package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation;

import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.EntityVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.StrictEntityVisitor;

@ThreadSafe
public class StrictEntityHierarchyVisitor<CONTEXT_TYPE extends VisitingContext> implements EntityVisitor<CONTEXT_TYPE> {

	private final Map<Class<? extends Entity>, StrictEntityVisitor<? extends Entity, CONTEXT_TYPE>> visitorLibrary;

	protected StrictEntityHierarchyVisitor(Map<Class<? extends Entity>, StrictEntityVisitor<? extends Entity, CONTEXT_TYPE>> visitorLibrary) {
		this.visitorLibrary = visitorLibrary;
	}

	@Override
	public void visit(Entity entity, CONTEXT_TYPE context) {
		if (visitorLibrary.containsKey(entity.getClass())) {
			final StrictEntityVisitor<? extends Entity, CONTEXT_TYPE> entityVisitor = visitorLibrary.get(entity.getClass());
			entityVisitor.visit(entity, context);
		} else {
			throw new RuntimeException("No visitor for " + entity.getClass()
				.getSimpleName() + " has been implemented yet");
		}
	}
}