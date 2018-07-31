package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldSimpleActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.StrictEntityVisitor;
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;

public class DisplayedNameVisitor<CONTEXT_TYPE extends VisitingContext> extends StrictEntityVisitor<DisplayedName, CONTEXT_TYPE> {
	private final VisitorFieldSimpleAction<DisplayedName, CONTEXT_TYPE> nameFieldAction;

	public DisplayedNameVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> fieldActionFactory) {
		super(preEntityActionFactory.getEntityPreAction(DisplayedName.class), postEntityActionFactory.getEntityPostAction(DisplayedName.class));
		nameFieldAction = fieldActionFactory.getSimpleFieldAction(DisplayedName.class, "name");
	}

	@Override
	protected void visitEntity(DisplayedName entity, CONTEXT_TYPE context) {
		nameFieldAction.performSimpleFieldAction(entity, context);
	}
}