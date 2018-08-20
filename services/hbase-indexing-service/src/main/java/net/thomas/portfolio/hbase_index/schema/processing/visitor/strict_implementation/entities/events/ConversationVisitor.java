package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.events;

import net.thomas.portfolio.hbase_index.schema.events.Conversation;
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

public class ConversationVisitor<CONTEXT_TYPE extends VisitingContext> extends StrictEntityVisitor<Conversation, CONTEXT_TYPE> {
	private final CommunicationEndpointVisitor<CONTEXT_TYPE> endpointVisitor;
	private final VisitorFieldPreAction<Conversation, CONTEXT_TYPE> primaryFieldPreAction;
	private final VisitorFieldPostAction<Conversation, CONTEXT_TYPE> primaryFieldPostAction;
	private final VisitorFieldPreAction<Conversation, CONTEXT_TYPE> secondaryFieldPreAction;
	private final VisitorFieldPostAction<Conversation, CONTEXT_TYPE> secondaryFieldPostAction;
	private final VisitorFieldSimpleAction<Conversation, CONTEXT_TYPE> timeOfEventFieldAction;
	private final VisitorFieldSimpleAction<Conversation, CONTEXT_TYPE> timeOfInterceptionFieldAction;
	private final VisitorFieldSimpleAction<Conversation, CONTEXT_TYPE> durationInSecondsFieldAction;
	private final VisitorFieldSimpleAction<Conversation, CONTEXT_TYPE> primaryLocationFieldAction;
	private final VisitorFieldSimpleAction<Conversation, CONTEXT_TYPE> secondaryLocationFieldAction;

	public ConversationVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> simpleFieldActionFactory,
			VisitorFieldPreActionFactory<CONTEXT_TYPE> preFieldActionFactory, VisitorFieldPostActionFactory<CONTEXT_TYPE> postFieldActionFactory,
			CommunicationEndpointVisitor<CONTEXT_TYPE> endpointVisitor) {
		super(preEntityActionFactory.getEntityPreAction(Conversation.class), postEntityActionFactory.getEntityPostAction(Conversation.class));
		this.endpointVisitor = endpointVisitor;
		timeOfEventFieldAction = simpleFieldActionFactory.getFieldSimpleAction(Conversation.class, "timeOfEvent");
		timeOfInterceptionFieldAction = simpleFieldActionFactory.getFieldSimpleAction(Conversation.class, "timeOfInterception");
		durationInSecondsFieldAction = simpleFieldActionFactory.getFieldSimpleAction(Conversation.class, "durationInSeconds");
		primaryLocationFieldAction = simpleFieldActionFactory.getFieldSimpleAction(Conversation.class, "primaryLocation");
		secondaryLocationFieldAction = simpleFieldActionFactory.getFieldSimpleAction(Conversation.class, "secondaryLocation");
		primaryFieldPreAction = preFieldActionFactory.getFieldPreAction(Conversation.class, "primary");
		primaryFieldPostAction = postFieldActionFactory.getFieldPostAction(Conversation.class, "primary");
		secondaryFieldPreAction = preFieldActionFactory.getFieldPreAction(Conversation.class, "secondary");
		secondaryFieldPostAction = postFieldActionFactory.getFieldPostAction(Conversation.class, "secondary");
	}

	@Override
	public void visitEntity(Conversation entity, CONTEXT_TYPE context) {
		timeOfEventFieldAction.performFieldSimpleAction(entity, context);
		timeOfInterceptionFieldAction.performFieldSimpleAction(entity, context);
		durationInSecondsFieldAction.performFieldSimpleAction(entity, context);
		if (entity.primaryLocation != null) {
			primaryLocationFieldAction.performFieldSimpleAction(entity, context);
		}
		if (entity.secondaryLocation != null) {
			secondaryLocationFieldAction.performFieldSimpleAction(entity, context);
		}
		if (entity.primary != null) {
			primaryFieldPreAction.performFieldPreAction(entity, context);
			endpointVisitor.visit(entity.primary, context);
			primaryFieldPostAction.performFieldPostAction(entity, context);
		}
		if (entity.secondary != null) {
			secondaryFieldPreAction.performFieldPreAction(entity, context);
			endpointVisitor.visit(entity.secondary, context);
			secondaryFieldPostAction.performFieldPostAction(entity, context);
		}
	}
}