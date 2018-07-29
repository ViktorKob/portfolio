package net.thomas.portfolio.hbase_index.schema.visitor.strict_implementation.entities.selectors;

import net.thomas.portfolio.hbase_index.schema.selectors.PrivateId;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorFieldSimpleActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.visitor.strict_implementation.entities.StrictEntityVisitor;

public class PrivateIdVisitor<CONTEXT_TYPE extends VisitingContext> extends StrictEntityVisitor<PrivateId, CONTEXT_TYPE> {
	private final VisitorFieldSimpleAction<PrivateId, CONTEXT_TYPE> numberFieldAction;

	public PrivateIdVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> fieldActionFactory) {
		super(preEntityActionFactory.getEntityPreAction(PrivateId.class), postEntityActionFactory.getEntityPostAction(PrivateId.class));
		numberFieldAction = fieldActionFactory.getSimpleFieldAction(PrivateId.class, "number");
	}

	@Override
	protected void visitEntity(PrivateId entity, CONTEXT_TYPE context) {
		numberFieldAction.performSimpleFieldAction(entity, context);
	}
}