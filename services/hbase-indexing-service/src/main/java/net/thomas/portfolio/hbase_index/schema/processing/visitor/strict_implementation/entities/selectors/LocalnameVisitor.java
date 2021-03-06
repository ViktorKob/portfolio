package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldSimpleActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.StrictEntityVisitor;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;

public class LocalnameVisitor<CONTEXT_TYPE extends VisitingContext> extends StrictEntityVisitor<Localname, CONTEXT_TYPE> {
	private final VisitorFieldSimpleAction<Localname, CONTEXT_TYPE> nameFieldAction;

	public LocalnameVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> fieldActionFactory) {
		super(preEntityActionFactory.getEntityPreAction(Localname.class), postEntityActionFactory.getEntityPostAction(Localname.class));
		nameFieldAction = fieldActionFactory.getFieldSimpleAction(Localname.class, "name");
	}

	@Override
	protected void visitEntity(Localname entity, CONTEXT_TYPE context) {
		nameFieldAction.performFieldSimpleAction(entity, context);
	}
}