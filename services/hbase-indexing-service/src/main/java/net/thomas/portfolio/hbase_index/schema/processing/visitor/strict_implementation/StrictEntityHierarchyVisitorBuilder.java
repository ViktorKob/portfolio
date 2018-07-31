package net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation;

import java.util.HashMap;
import java.util.Map;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.events.Conversation;
import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.events.TextMessage;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPreAction;
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
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.events.ConversationVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.events.EmailVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.events.TextMessageVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.meta_entities.CommunicationEndpointVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.meta_entities.EmailEndpointVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors.DisplayedNameVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors.DomainVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors.EmailAddressVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors.LocalnameVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors.PrivateIdVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.entities.selectors.PublicIdVisitor;
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;
import net.thomas.portfolio.hbase_index.schema.selectors.Domain;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;
import net.thomas.portfolio.hbase_index.schema.selectors.PrivateId;
import net.thomas.portfolio.hbase_index.schema.selectors.PublicId;

public class StrictEntityHierarchyVisitorBuilder<CONTEXT_TYPE extends VisitingContext> {
	private VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory;
	private VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory;
	private VisitorFieldSimpleActionFactory<CONTEXT_TYPE> simpleFieldActionFactory;
	private VisitorFieldPreActionFactory<CONTEXT_TYPE> preFieldActionFactory;
	private VisitorFieldPostActionFactory<CONTEXT_TYPE> postFieldActionFactory;

	public StrictEntityHierarchyVisitorBuilder() {
		preEntityActionFactory = new VisitorEntityPreActionFactory<CONTEXT_TYPE>() {
			@Override
			public <T extends Entity> VisitorEntityPreAction<T, CONTEXT_TYPE> getEntityPreAction(Class<T> entityClass) {
				return (entity, context) -> {
				};
			}
		};
		postEntityActionFactory = new VisitorEntityPostActionFactory<CONTEXT_TYPE>() {
			@Override
			public <T extends Entity> VisitorEntityPostAction<T, CONTEXT_TYPE> getEntityPostAction(Class<T> entityClass) {
				return (entity, context) -> {
				};
			}
		};
		simpleFieldActionFactory = new VisitorFieldSimpleActionFactory<CONTEXT_TYPE>() {
			@Override
			public <T extends Entity> VisitorFieldSimpleAction<T, CONTEXT_TYPE> getSimpleFieldAction(Class<T> entityClass, String field) {
				return (entity, context) -> {
				};
			}
		};
		preFieldActionFactory = new VisitorFieldPreActionFactory<CONTEXT_TYPE>() {
			@Override
			public <T extends Entity> VisitorFieldPreAction<T, CONTEXT_TYPE> getFieldPreAction(Class<T> entityClass, String field) {
				return (entity, context) -> {
				};
			}
		};
		postFieldActionFactory = new VisitorFieldPostActionFactory<CONTEXT_TYPE>() {
			@Override
			public <T extends Entity> VisitorFieldPostAction<T, CONTEXT_TYPE> getFieldPostAction(Class<T> entityClass, String field) {
				return (entity, context) -> {
				};
			}
		};
	}

	public StrictEntityHierarchyVisitorBuilder<CONTEXT_TYPE> setEntityPreActionFactory(VisitorEntityPreActionFactory<CONTEXT_TYPE> actionFactory) {
		preEntityActionFactory = actionFactory;
		return this;
	}

	public StrictEntityHierarchyVisitorBuilder<CONTEXT_TYPE> setEntityPostActionFactory(VisitorEntityPostActionFactory<CONTEXT_TYPE> actionFactory) {
		postEntityActionFactory = actionFactory;
		return this;
	}

	public StrictEntityHierarchyVisitorBuilder<CONTEXT_TYPE> setFieldSimpleActionFactory(VisitorFieldSimpleActionFactory<CONTEXT_TYPE> actionFactory) {
		simpleFieldActionFactory = actionFactory;
		return this;
	}

	public StrictEntityHierarchyVisitorBuilder<CONTEXT_TYPE> setFieldPreActionFactory(VisitorFieldPreActionFactory<CONTEXT_TYPE> actionFactory) {
		preFieldActionFactory = actionFactory;
		return this;
	}

	public StrictEntityHierarchyVisitorBuilder<CONTEXT_TYPE> setFieldPostActionFactory(VisitorFieldPostActionFactory<CONTEXT_TYPE> actionFactory) {
		postFieldActionFactory = actionFactory;
		return this;
	}

	@SuppressWarnings("unchecked")
	public StrictEntityHierarchyVisitor<CONTEXT_TYPE> build() {
		final Map<Class<? extends Entity>, StrictEntityVisitor<? extends Entity, CONTEXT_TYPE>> visitorLibrary = new HashMap<>();
		visitorLibrary.put(Localname.class, new LocalnameVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory));
		visitorLibrary.put(DisplayedName.class, new DisplayedNameVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory));
		visitorLibrary.put(Domain.class,
				new DomainVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory, preFieldActionFactory, postFieldActionFactory));
		visitorLibrary.put(EmailAddress.class,
				new EmailAddressVisitor<>(preEntityActionFactory, postEntityActionFactory, preFieldActionFactory, postFieldActionFactory,
						(LocalnameVisitor<CONTEXT_TYPE>) visitorLibrary.get(Localname.class), (DomainVisitor<CONTEXT_TYPE>) visitorLibrary.get(Domain.class)));
		visitorLibrary.put(PublicId.class, new PublicIdVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory));
		visitorLibrary.put(PrivateId.class, new PrivateIdVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory));
		visitorLibrary.put(EmailEndpoint.class,
				new EmailEndpointVisitor<>(preEntityActionFactory, postEntityActionFactory, preFieldActionFactory, postFieldActionFactory,
						(DisplayedNameVisitor<CONTEXT_TYPE>) visitorLibrary.get(DisplayedName.class),
						(EmailAddressVisitor<CONTEXT_TYPE>) visitorLibrary.get(EmailAddress.class)));
		visitorLibrary.put(CommunicationEndpoint.class,
				new CommunicationEndpointVisitor<>(preEntityActionFactory, postEntityActionFactory, preFieldActionFactory, postFieldActionFactory,
						(PublicIdVisitor<CONTEXT_TYPE>) visitorLibrary.get(PublicId.class),
						(PrivateIdVisitor<CONTEXT_TYPE>) visitorLibrary.get(PrivateId.class)));
		visitorLibrary.put(Email.class, new EmailVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory, preFieldActionFactory,
				postFieldActionFactory, (EmailEndpointVisitor<CONTEXT_TYPE>) visitorLibrary.get(EmailEndpoint.class)));
		visitorLibrary.put(TextMessage.class, new TextMessageVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory,
				preFieldActionFactory, postFieldActionFactory, (CommunicationEndpointVisitor<CONTEXT_TYPE>) visitorLibrary.get(CommunicationEndpoint.class)));
		visitorLibrary.put(Conversation.class, new ConversationVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory,
				preFieldActionFactory, postFieldActionFactory, (CommunicationEndpointVisitor<CONTEXT_TYPE>) visitorLibrary.get(CommunicationEndpoint.class)));
		return new StrictEntityHierarchyVisitor<>(visitorLibrary);
	}
}