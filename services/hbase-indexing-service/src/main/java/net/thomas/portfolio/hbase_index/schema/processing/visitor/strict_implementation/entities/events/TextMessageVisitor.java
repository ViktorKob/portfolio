package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.events;

import net.thomas.portfolio.hbase_index.schema.events.TextMessage;
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
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.meta_entities.CommunicationEndpointVisitor;

public class TextMessageVisitor<CONTEXT_TYPE extends VisitingContext> extends StrictEntityVisitor<TextMessage, CONTEXT_TYPE> {
	private final CommunicationEndpointVisitor<CONTEXT_TYPE> endpointVisitor;
	private final VisitorFieldPreAction<TextMessage, CONTEXT_TYPE> senderFieldPreAction;
	private final VisitorFieldPostAction<TextMessage, CONTEXT_TYPE> senderFieldPostAction;
	private final VisitorFieldPreAction<TextMessage, CONTEXT_TYPE> receiverFieldPreAction;
	private final VisitorFieldPostAction<TextMessage, CONTEXT_TYPE> receiverFieldPostAction;
	private final VisitorFieldSimpleAction<TextMessage, CONTEXT_TYPE> timeOfEventFieldAction;
	private final VisitorFieldSimpleAction<TextMessage, CONTEXT_TYPE> timeOfInterceptionFieldAction;
	private final VisitorFieldSimpleAction<TextMessage, CONTEXT_TYPE> messageFieldAction;
	private final VisitorFieldSimpleAction<TextMessage, CONTEXT_TYPE> senderLocationFieldAction;
	private final VisitorFieldSimpleAction<TextMessage, CONTEXT_TYPE> receiverLocationFieldAction;

	public TextMessageVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> simpleFieldActionFactory,
			VisitorFieldPreActionFactory<CONTEXT_TYPE> preFieldActionFactory, VisitorFieldPostActionFactory<CONTEXT_TYPE> postFieldActionFactory,
			CommunicationEndpointVisitor<CONTEXT_TYPE> endpointVisitor) {
		super(preEntityActionFactory.getEntityPreAction(TextMessage.class), postEntityActionFactory.getEntityPostAction(TextMessage.class));
		this.endpointVisitor = endpointVisitor;
		senderFieldPreAction = preFieldActionFactory.getFieldPreAction(TextMessage.class, "sender");
		senderFieldPostAction = postFieldActionFactory.getFieldPostAction(TextMessage.class, "sender");
		receiverFieldPreAction = preFieldActionFactory.getFieldPreAction(TextMessage.class, "receiver");
		receiverFieldPostAction = postFieldActionFactory.getFieldPostAction(TextMessage.class, "receiver");
		timeOfEventFieldAction = simpleFieldActionFactory.getSimpleFieldAction(TextMessage.class, "timeOfEvent");
		timeOfInterceptionFieldAction = simpleFieldActionFactory.getSimpleFieldAction(TextMessage.class, "timeOfInterception");
		messageFieldAction = simpleFieldActionFactory.getSimpleFieldAction(TextMessage.class, "message");
		senderLocationFieldAction = simpleFieldActionFactory.getSimpleFieldAction(TextMessage.class, "senderLocation");
		receiverLocationFieldAction = simpleFieldActionFactory.getSimpleFieldAction(TextMessage.class, "receiverLocation");

	}

	@Override
	public void visitEntity(TextMessage entity, CONTEXT_TYPE context) {
		timeOfEventFieldAction.performSimpleFieldAction(entity, context);
		timeOfInterceptionFieldAction.performSimpleFieldAction(entity, context);
		messageFieldAction.performSimpleFieldAction(entity, context);
		if (entity.senderLocation != null) {
			senderLocationFieldAction.performSimpleFieldAction(entity, context);
		}
		if (entity.receiverLocation != null) {
			receiverLocationFieldAction.performSimpleFieldAction(entity, context);
		}
		if (entity.sender != null) {
			senderFieldPreAction.performFieldPreAction(entity, context);
			endpointVisitor.visit(entity.sender, context);
			senderFieldPostAction.performFieldPostAction(entity, context);
		}
		if (entity.receiver != null) {
			receiverFieldPreAction.performFieldPreAction(entity, context);
			endpointVisitor.visit(entity.receiver, context);
			receiverFieldPostAction.performFieldPostAction(entity, context);
		}
	}
}