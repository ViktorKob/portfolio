package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.events;

import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
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
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.meta_entities.EmailEndpointVisitor;

public class EmailVisitor<CONTEXT_TYPE extends VisitingContext> extends StrictEntityVisitor<Email, CONTEXT_TYPE> {
	private final EmailEndpointVisitor<CONTEXT_TYPE> endpointVisitor;
	private final VisitorFieldPreAction<Email, CONTEXT_TYPE> fromFieldPreAction;
	private final VisitorFieldPostAction<Email, CONTEXT_TYPE> fromFieldPostAction;
	private final VisitorFieldPreAction<Email, CONTEXT_TYPE> toFieldPreAction;
	private final VisitorFieldPostAction<Email, CONTEXT_TYPE> toFieldPostAction;
	private final VisitorFieldPreAction<Email, CONTEXT_TYPE> ccFieldPreAction;
	private final VisitorFieldPostAction<Email, CONTEXT_TYPE> ccFieldPostAction;
	private final VisitorFieldPreAction<Email, CONTEXT_TYPE> bccFieldPreAction;
	private final VisitorFieldPostAction<Email, CONTEXT_TYPE> bccFieldPostAction;
	private final VisitorFieldSimpleAction<Email, CONTEXT_TYPE> timeOfEventFieldAction;
	private final VisitorFieldSimpleAction<Email, CONTEXT_TYPE> timeOfInterceptionFieldAction;
	private final VisitorFieldSimpleAction<Email, CONTEXT_TYPE> subjectFieldAction;
	private final VisitorFieldSimpleAction<Email, CONTEXT_TYPE> messageFieldAction;

	public EmailVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> simpleFieldActionFactory,
			VisitorFieldPreActionFactory<CONTEXT_TYPE> preFieldActionFactory, VisitorFieldPostActionFactory<CONTEXT_TYPE> postFieldActionFactory,
			EmailEndpointVisitor<CONTEXT_TYPE> endpointVisitor) {
		super(preEntityActionFactory.getEntityPreAction(Email.class), postEntityActionFactory.getEntityPostAction(Email.class));
		this.endpointVisitor = endpointVisitor;
		fromFieldPreAction = preFieldActionFactory.getFieldPreAction(Email.class, "from");
		fromFieldPostAction = postFieldActionFactory.getFieldPostAction(Email.class, "from");
		toFieldPreAction = preFieldActionFactory.getFieldPreAction(Email.class, "to");
		toFieldPostAction = postFieldActionFactory.getFieldPostAction(Email.class, "to");
		ccFieldPreAction = preFieldActionFactory.getFieldPreAction(Email.class, "cc");
		ccFieldPostAction = postFieldActionFactory.getFieldPostAction(Email.class, "cc");
		bccFieldPreAction = preFieldActionFactory.getFieldPreAction(Email.class, "bcc");
		bccFieldPostAction = postFieldActionFactory.getFieldPostAction(Email.class, "bcc");
		timeOfEventFieldAction = simpleFieldActionFactory.getSimpleFieldAction(Email.class, "timeOfEvent");
		timeOfInterceptionFieldAction = simpleFieldActionFactory.getSimpleFieldAction(Email.class, "timeOfInterception");
		subjectFieldAction = simpleFieldActionFactory.getSimpleFieldAction(Email.class, "subject");
		messageFieldAction = simpleFieldActionFactory.getSimpleFieldAction(Email.class, "message");

	}

	@Override
	public void visitEntity(Email entity, CONTEXT_TYPE context) {
		timeOfEventFieldAction.performSimpleFieldAction(entity, context);
		timeOfInterceptionFieldAction.performSimpleFieldAction(entity, context);
		subjectFieldAction.performSimpleFieldAction(entity, context);
		fromFieldPreAction.performFieldPreAction(entity, context);
		endpointVisitor.visit(entity.from, context);
		fromFieldPostAction.performFieldPostAction(entity, context);
		toFieldPreAction.performFieldPreAction(entity, context);
		for (final EmailEndpoint endpoint : entity.to) {
			endpointVisitor.visit(endpoint, context);
		}
		toFieldPostAction.performFieldPostAction(entity, context);
		ccFieldPreAction.performFieldPreAction(entity, context);
		for (final EmailEndpoint endpoint : entity.cc) {
			endpointVisitor.visit(endpoint, context);
		}
		ccFieldPostAction.performFieldPostAction(entity, context);
		bccFieldPreAction.performFieldPreAction(entity, context);
		for (final EmailEndpoint endpoint : entity.bcc) {
			endpointVisitor.visit(endpoint, context);
		}
		bccFieldPostAction.performFieldPostAction(entity, context);
		messageFieldAction.performSimpleFieldAction(entity, context);
	}
}