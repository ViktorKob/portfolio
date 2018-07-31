package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.meta_entities;

import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.StrictEntityVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors.DisplayedNameVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors.EmailAddressVisitor;

public class EmailEndpointVisitor<CONTEXT_TYPE extends VisitingContext> extends StrictEntityVisitor<EmailEndpoint, CONTEXT_TYPE> {
	private final DisplayedNameVisitor<CONTEXT_TYPE> displayedNameVisitor;
	private final EmailAddressVisitor<CONTEXT_TYPE> addressVisitor;
	private final VisitorFieldPreAction<EmailEndpoint, CONTEXT_TYPE> displayedNameFieldPreAction;
	private final VisitorFieldPostAction<EmailEndpoint, CONTEXT_TYPE> displayedNameFieldPostAction;
	private final VisitorFieldPreAction<EmailEndpoint, CONTEXT_TYPE> addressFieldPreAction;
	private final VisitorFieldPostAction<EmailEndpoint, CONTEXT_TYPE> addressFieldPostAction;

	public EmailEndpointVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldPreActionFactory<CONTEXT_TYPE> preFieldActionFactory,
			VisitorFieldPostActionFactory<CONTEXT_TYPE> postFieldActionFactory, DisplayedNameVisitor<CONTEXT_TYPE> displayedNameVisitor,
			EmailAddressVisitor<CONTEXT_TYPE> addressVisitor) {
		super(preEntityActionFactory.getEntityPreAction(EmailEndpoint.class), postEntityActionFactory.getEntityPostAction(EmailEndpoint.class));
		this.displayedNameVisitor = displayedNameVisitor;
		this.addressVisitor = addressVisitor;
		displayedNameFieldPreAction = preFieldActionFactory.getFieldPreAction(EmailEndpoint.class, "displayedName");
		displayedNameFieldPostAction = postFieldActionFactory.getFieldPostAction(EmailEndpoint.class, "displayedName");
		addressFieldPreAction = preFieldActionFactory.getFieldPreAction(EmailEndpoint.class, "address");
		addressFieldPostAction = postFieldActionFactory.getFieldPostAction(EmailEndpoint.class, "address");
	}

	@Override
	public void visitEntity(EmailEndpoint entity, CONTEXT_TYPE context) {
		if (entity.displayedName != null) {
			displayedNameFieldPreAction.performFieldPreAction(entity, context);
			displayedNameVisitor.visit(entity.displayedName, context);
			displayedNameFieldPostAction.performFieldPostAction(entity, context);
		}
		if (entity.address != null) {
			addressFieldPreAction.performFieldPreAction(entity, context);
			addressVisitor.visit(entity.address, context);
			addressFieldPostAction.performFieldPostAction(entity, context);
		}
	}
}