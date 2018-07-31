package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

public abstract class StrictEntityVisitor<TYPE extends Entity, CONTEXT_TYPE extends VisitingContext> {
	protected final VisitorEntityPreAction<TYPE, CONTEXT_TYPE> preAction;
	protected final VisitorEntityPostAction<TYPE, CONTEXT_TYPE> postAction;

	public StrictEntityVisitor(VisitorEntityPreAction<TYPE, CONTEXT_TYPE> preAction, VisitorEntityPostAction<TYPE, CONTEXT_TYPE> postAction) {
		this.preAction = preAction;
		this.postAction = postAction;
	}

	// TODO[Thomas]: Determine exact type erasure to remove compile error without hack
	@SuppressWarnings("unchecked")
	public void visit(Entity entity, CONTEXT_TYPE context) {
		if (entity != null) {
			if (preAction != null) {
				preAction.performEntityPreAction((TYPE) entity, context);
			}
			visitEntity((TYPE) entity, context);
			if (postAction != null) {
				postAction.performEntityPostAction((TYPE) entity, context);
			}
		}
	}

	protected abstract void visitEntity(TYPE entity, CONTEXT_TYPE context);
}