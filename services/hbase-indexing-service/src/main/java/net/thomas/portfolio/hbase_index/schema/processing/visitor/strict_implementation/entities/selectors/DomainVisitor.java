package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldSimpleActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.StrictEntityVisitor;
import net.thomas.portfolio.hbase_index.schema.selectors.Domain;

public class DomainVisitor<CONTEXT_TYPE extends VisitingContext> extends StrictEntityVisitor<Domain, CONTEXT_TYPE> {
	private final VisitorFieldSimpleAction<Domain, CONTEXT_TYPE> domainPartFieldAction;
	private final VisitorFieldPreAction<Domain, CONTEXT_TYPE> domainFieldPreAction;
	private final VisitorFieldPostAction<Domain, CONTEXT_TYPE> domainFieldPostAction;

	public DomainVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> simpleFieldActionFactory,
			VisitorFieldPreActionFactory<CONTEXT_TYPE> preFieldActionFactory, VisitorFieldPostActionFactory<CONTEXT_TYPE> postFieldActionFactory) {
		super(preEntityActionFactory.getEntityPreAction(Domain.class), postEntityActionFactory.getEntityPostAction(Domain.class));
		domainPartFieldAction = simpleFieldActionFactory.getSimpleFieldAction(Domain.class, "domainPart");
		domainFieldPreAction = preFieldActionFactory.getFieldPreAction(Domain.class, "domain");
		domainFieldPostAction = postFieldActionFactory.getFieldPostAction(Domain.class, "domain");
	}

	@Override
	public void visitEntity(Domain entity, CONTEXT_TYPE context) {
		domainPartFieldAction.performSimpleFieldAction(entity, context);
		if (entity.domain != null) {
			domainFieldPreAction.performFieldPreAction(entity, context);
			visit(entity.domain, context);
			domainFieldPostAction.performFieldPostAction(entity, context);
		}
	}
}