package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.StrictEntityVisitor;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;

public class EmailAddressVisitor<CONTEXT_TYPE extends VisitingContext> extends StrictEntityVisitor<EmailAddress, CONTEXT_TYPE> {
	private final LocalnameVisitor<CONTEXT_TYPE> localnameVisitor;
	private final DomainVisitor<CONTEXT_TYPE> domainVisitor;
	private final VisitorFieldPreAction<EmailAddress, CONTEXT_TYPE> localnameFieldPreAction;
	private final VisitorFieldPostAction<EmailAddress, CONTEXT_TYPE> localnameFieldPostAction;
	private final VisitorFieldPreAction<EmailAddress, CONTEXT_TYPE> domainFieldPreAction;
	private final VisitorFieldPostAction<EmailAddress, CONTEXT_TYPE> domainFieldPostAction;

	public EmailAddressVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldPreActionFactory<CONTEXT_TYPE> preFieldActionFactory,
			VisitorFieldPostActionFactory<CONTEXT_TYPE> postFieldActionFactory, LocalnameVisitor<CONTEXT_TYPE> localnameVisitor,
			DomainVisitor<CONTEXT_TYPE> domainVisitor) {
		super(preEntityActionFactory.getEntityPreAction(EmailAddress.class), postEntityActionFactory.getEntityPostAction(EmailAddress.class));
		this.localnameVisitor = localnameVisitor;
		this.domainVisitor = domainVisitor;
		localnameFieldPreAction = preFieldActionFactory.getFieldPreAction(EmailAddress.class, "localname");
		localnameFieldPostAction = postFieldActionFactory.getFieldPostAction(EmailAddress.class, "localname");
		domainFieldPreAction = preFieldActionFactory.getFieldPreAction(EmailAddress.class, "domain");
		domainFieldPostAction = postFieldActionFactory.getFieldPostAction(EmailAddress.class, "domain");
	}

	@Override
	public void visitEntity(EmailAddress entity, CONTEXT_TYPE context) {
		if (entity.localname != null) {

			localnameFieldPreAction.performFieldPreAction(entity, context);
			localnameVisitor.visit(entity.localname, context);
			localnameFieldPostAction.performFieldPostAction(entity, context);
		}
		if (entity.domain != null) {
			domainFieldPreAction.performFieldPreAction(entity, context);
			domainVisitor.visit(entity.domain, context);
			domainFieldPostAction.performFieldPostAction(entity, context);
		}
	}
}