package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.meta_entities;

import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.StrictEntityVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors.PrivateIdVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors.PublicIdVisitor;

public class CommunicationEndpointVisitor<CONTEXT_TYPE extends VisitingContext> extends StrictEntityVisitor<CommunicationEndpoint, CONTEXT_TYPE> {
	private final PublicIdVisitor<CONTEXT_TYPE> publicIdVisitor;
	private final PrivateIdVisitor<CONTEXT_TYPE> privateIdVisitor;
	private final VisitorFieldPreAction<CommunicationEndpoint, CONTEXT_TYPE> publicIdFieldPreAction;
	private final VisitorFieldPostAction<CommunicationEndpoint, CONTEXT_TYPE> publicIdFieldPostAction;
	private final VisitorFieldPreAction<CommunicationEndpoint, CONTEXT_TYPE> privateIdFieldPreAction;
	private final VisitorFieldPostAction<CommunicationEndpoint, CONTEXT_TYPE> privateIdFieldPostAction;

	public CommunicationEndpointVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldPreActionFactory<CONTEXT_TYPE> preFieldActionFactory,
			VisitorFieldPostActionFactory<CONTEXT_TYPE> postFieldActionFactory, PublicIdVisitor<CONTEXT_TYPE> publicIdVisitor,
			PrivateIdVisitor<CONTEXT_TYPE> privateIdVisitor) {
		super(preEntityActionFactory.getEntityPreAction(CommunicationEndpoint.class), postEntityActionFactory.getEntityPostAction(CommunicationEndpoint.class));
		this.publicIdVisitor = publicIdVisitor;
		this.privateIdVisitor = privateIdVisitor;
		publicIdFieldPreAction = preFieldActionFactory.getFieldPreAction(CommunicationEndpoint.class, "publicId");
		publicIdFieldPostAction = postFieldActionFactory.getFieldPostAction(CommunicationEndpoint.class, "publicId");
		privateIdFieldPreAction = preFieldActionFactory.getFieldPreAction(CommunicationEndpoint.class, "privateId");
		privateIdFieldPostAction = postFieldActionFactory.getFieldPostAction(CommunicationEndpoint.class, "privateId");
	}

	@Override
	public void visitEntity(CommunicationEndpoint entity, CONTEXT_TYPE context) {
		if (entity.publicId != null) {
			publicIdFieldPreAction.performFieldPreAction(entity, context);
			publicIdVisitor.visit(entity.publicId, context);
			publicIdFieldPostAction.performFieldPostAction(entity, context);
		}
		if (entity.privateId != null) {
			privateIdFieldPreAction.performFieldPreAction(entity, context);
			privateIdVisitor.visit(entity.privateId, context);
			privateIdFieldPostAction.performFieldPostAction(entity, context);
		}
	}
}