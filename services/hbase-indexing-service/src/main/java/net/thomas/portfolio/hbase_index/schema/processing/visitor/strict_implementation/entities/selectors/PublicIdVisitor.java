package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldSimpleActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.StrictEntityVisitor;
import net.thomas.portfolio.hbase_index.schema.selectors.PublicId;

public class PublicIdVisitor<CONTEXT_TYPE extends VisitingContext> extends StrictEntityVisitor<PublicId, CONTEXT_TYPE> {
	private final VisitorFieldSimpleAction<PublicId, CONTEXT_TYPE> numberFieldAction;

	public PublicIdVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> fieldActionFactory) {
		super(preEntityActionFactory.getEntityPreAction(PublicId.class), postEntityActionFactory.getEntityPostAction(PublicId.class));
		numberFieldAction = fieldActionFactory.getSimpleFieldAction(PublicId.class, "number");
	}

	@Override
	protected void visitEntity(PublicId entity, CONTEXT_TYPE context) {
		numberFieldAction.performSimpleFieldAction(entity, context);
	}
}