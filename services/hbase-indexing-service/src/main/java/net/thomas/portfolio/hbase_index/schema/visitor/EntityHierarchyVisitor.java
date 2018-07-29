package net.thomas.portfolio.hbase_index.schema.visitor;

import java.util.HashMap;
import java.util.Map;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.documents.Conversation;
import net.thomas.portfolio.hbase_index.schema.documents.Email;
import net.thomas.portfolio.hbase_index.schema.documents.Event;
import net.thomas.portfolio.hbase_index.schema.documents.TextMessage;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;
import net.thomas.portfolio.hbase_index.schema.selectors.Domain;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;
import net.thomas.portfolio.hbase_index.schema.selectors.PrivateId;
import net.thomas.portfolio.hbase_index.schema.selectors.PublicId;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitorEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitorFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitorFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitorFieldSimpleAction;

public class EntityHierarchyVisitor<CONTEXT_TYPE extends VisitingContext> {

	private final Map<Class<? extends Entity>, EntityVisitor<? extends Entity, CONTEXT_TYPE>> visitorLibrary;

	private EntityHierarchyVisitor(Map<Class<? extends Entity>, EntityVisitor<? extends Entity, CONTEXT_TYPE>> visitorLibrary) {
		this.visitorLibrary = visitorLibrary;
	}

	public void visit(Entity entity, CONTEXT_TYPE context) {
		if (visitorLibrary.containsKey(entity.getClass())) {
			final EntityVisitor<? extends Entity, CONTEXT_TYPE> entityVisitor = visitorLibrary.get(entity.getClass());
			entityVisitor.visit(entity, context);
		} else {
			throw new RuntimeException("No visitor for " + entity.getClass()
				.getSimpleName() + " has been implemented yet");
		}
	}

	public static class EntityHierarchyVisitorBuilder<CONTEXT_TYPE extends VisitingContext> {
		private VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory;
		private VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory;
		private VisitorFieldSimpleActionFactory<CONTEXT_TYPE> simpleFieldActionFactory;
		private VisitorFieldPreActionFactory<CONTEXT_TYPE> preFieldActionFactory;
		private VisitorFieldPostActionFactory<CONTEXT_TYPE> postFieldActionFactory;

		public EntityHierarchyVisitorBuilder() {
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
				public <T extends Entity> VisitorFieldSimpleAction<T, CONTEXT_TYPE> getFieldAction(Class<T> entityClass, String field) {
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

		public EntityHierarchyVisitorBuilder<CONTEXT_TYPE> setEntityPreActionFactory(VisitorEntityPreActionFactory<CONTEXT_TYPE> actionFactory) {
			preEntityActionFactory = actionFactory;
			return this;
		}

		public EntityHierarchyVisitorBuilder<CONTEXT_TYPE> setEntityPostActionFactory(VisitorEntityPostActionFactory<CONTEXT_TYPE> actionFactory) {
			postEntityActionFactory = actionFactory;
			return this;
		}

		public EntityHierarchyVisitorBuilder<CONTEXT_TYPE> setFieldSimpleActionFactory(VisitorFieldSimpleActionFactory<CONTEXT_TYPE> actionFactory) {
			simpleFieldActionFactory = actionFactory;
			return this;
		}

		public EntityHierarchyVisitorBuilder<CONTEXT_TYPE> setFieldPreActionFactory(VisitorFieldPreActionFactory<CONTEXT_TYPE> actionFactory) {
			preFieldActionFactory = actionFactory;
			return this;
		}

		public EntityHierarchyVisitorBuilder<CONTEXT_TYPE> setFieldPostActionFactory(VisitorFieldPostActionFactory<CONTEXT_TYPE> actionFactory) {
			postFieldActionFactory = actionFactory;
			return this;
		}

		@SuppressWarnings("unchecked")
		public EntityHierarchyVisitor<CONTEXT_TYPE> build() {
			// final Map<Class<? extends Entity>, EntityVisitor<Entity, CONTEXT_TYPE>> visitorLibrary = new HashMap<>();
			final Map<Class<? extends Entity>, EntityVisitor<? extends Entity, CONTEXT_TYPE>> visitorLibrary = new HashMap<>();
			visitorLibrary.put(Localname.class, new LocalnameVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory));
			visitorLibrary.put(DisplayedName.class, new DisplayedNameVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory));
			visitorLibrary.put(Domain.class, new DomainVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory,
					preFieldActionFactory, postFieldActionFactory));
			visitorLibrary.put(EmailAddress.class,
					new EmailAddressVisitor<>(preEntityActionFactory, postEntityActionFactory, preFieldActionFactory, postFieldActionFactory,
							(LocalnameVisitor<CONTEXT_TYPE>) visitorLibrary.get(Localname.class),
							(DomainVisitor<CONTEXT_TYPE>) visitorLibrary.get(Domain.class)));
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
			visitorLibrary.put(TextMessage.class,
					new TextMessageVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory, preFieldActionFactory,
							postFieldActionFactory, (CommunicationEndpointVisitor<CONTEXT_TYPE>) visitorLibrary.get(CommunicationEndpoint.class)));
			visitorLibrary.put(Conversation.class,
					new ConversationVisitor<>(preEntityActionFactory, postEntityActionFactory, simpleFieldActionFactory, preFieldActionFactory,
							postFieldActionFactory, (CommunicationEndpointVisitor<CONTEXT_TYPE>) visitorLibrary.get(CommunicationEndpoint.class)));
			return new EntityHierarchyVisitor<>(visitorLibrary);
		}
	}

	public static interface VisitingContext {
	}

	public static class BlankVisitingContext implements VisitingContext {
	}

	public static class EventContext implements VisitingContext {
		public final Event source;

		public EventContext(Event source) {
			this.source = source;
		}
	}

	public static class PathContext extends EventContext {
		public String path;

		public PathContext(Event source) {
			super(source);
		}
	}

	public static interface VisitorEntityAction<ENTITY_TYPE extends Entity, CONTEXT_TYPE extends VisitingContext> {
	}

	@FunctionalInterface
	public static interface VisitorEntityPreAction<ENTITY_TYPE extends Entity, CONTEXT_TYPE extends VisitingContext>
			extends VisitorEntityAction<ENTITY_TYPE, CONTEXT_TYPE> {
		void performEntityPreAction(ENTITY_TYPE entity, CONTEXT_TYPE context);
	}

	@FunctionalInterface
	public static interface VisitorEntityPostAction<ENTITY_TYPE extends Entity, CONTEXT_TYPE extends VisitingContext>
			extends VisitorEntityAction<ENTITY_TYPE, CONTEXT_TYPE> {
		void performEntityPostAction(ENTITY_TYPE entity, CONTEXT_TYPE context);
	}

	public static interface VisitorFieldAction<ENTITY_TYPE extends Entity, CONTEXT_TYPE extends VisitingContext> {
	}

	@FunctionalInterface
	public static interface VisitorFieldSimpleAction<ENTITY_TYPE extends Entity, CONTEXT_TYPE extends VisitingContext>
			extends VisitorFieldAction<ENTITY_TYPE, CONTEXT_TYPE> {
		void performFieldAction(ENTITY_TYPE entity, CONTEXT_TYPE context);
	}

	@FunctionalInterface
	public static interface VisitorFieldPreAction<ENTITY_TYPE extends Entity, CONTEXT_TYPE extends VisitingContext>
			extends VisitorFieldAction<ENTITY_TYPE, CONTEXT_TYPE> {
		void performFieldPreAction(ENTITY_TYPE entity, CONTEXT_TYPE context);
	}

	@FunctionalInterface
	public static interface VisitorFieldPostAction<ENTITY_TYPE extends Entity, CONTEXT_TYPE extends VisitingContext>
			extends VisitorFieldAction<ENTITY_TYPE, CONTEXT_TYPE> {
		void performFieldPostAction(ENTITY_TYPE entity, CONTEXT_TYPE context);
	}
}

abstract class EntityVisitor<TYPE extends Entity, CONTEXT_TYPE extends VisitingContext> {
	protected final VisitorEntityPreAction<TYPE, CONTEXT_TYPE> preAction;
	protected final VisitorEntityPostAction<TYPE, CONTEXT_TYPE> postAction;

	public EntityVisitor(VisitorEntityPreAction<TYPE, CONTEXT_TYPE> preAction, VisitorEntityPostAction<TYPE, CONTEXT_TYPE> postAction) {
		this.preAction = preAction;
		this.postAction = postAction;
	}

	// TODO[Thomas]: Determine exact type erasure to remove compile error without hack
	@SuppressWarnings("unchecked")
	public void visit(Entity entity, CONTEXT_TYPE context) {
		if (entity != null) {
			if (preAction != null) {
				preAction.performEntityPreAction((TYPE) entity, context);
			}
			visitEntity((TYPE) entity, context);
			if (postAction != null) {
				postAction.performEntityPostAction((TYPE) entity, context);
			}
		}
	}

	protected abstract void visitEntity(TYPE entity, CONTEXT_TYPE context);
}

class EmailVisitor<CONTEXT_TYPE extends VisitingContext> extends EntityVisitor<Email, CONTEXT_TYPE> {
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
		timeOfEventFieldAction = simpleFieldActionFactory.getFieldAction(Email.class, "timeOfEvent");
		timeOfInterceptionFieldAction = simpleFieldActionFactory.getFieldAction(Email.class, "timeOfInterception");
		subjectFieldAction = simpleFieldActionFactory.getFieldAction(Email.class, "subject");
		messageFieldAction = simpleFieldActionFactory.getFieldAction(Email.class, "message");

	}

	@Override
	public void visitEntity(Email entity, CONTEXT_TYPE context) {
		timeOfEventFieldAction.performFieldAction(entity, context);
		timeOfInterceptionFieldAction.performFieldAction(entity, context);
		subjectFieldAction.performFieldAction(entity, context);
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
		messageFieldAction.performFieldAction(entity, context);
	}
}

class TextMessageVisitor<CONTEXT_TYPE extends VisitingContext> extends EntityVisitor<TextMessage, CONTEXT_TYPE> {
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
		timeOfEventFieldAction = simpleFieldActionFactory.getFieldAction(TextMessage.class, "timeOfEvent");
		timeOfInterceptionFieldAction = simpleFieldActionFactory.getFieldAction(TextMessage.class, "timeOfInterception");
		messageFieldAction = simpleFieldActionFactory.getFieldAction(TextMessage.class, "message");
		senderLocationFieldAction = simpleFieldActionFactory.getFieldAction(TextMessage.class, "senderLocation");
		receiverLocationFieldAction = simpleFieldActionFactory.getFieldAction(TextMessage.class, "receiverLocation");

	}

	@Override
	public void visitEntity(TextMessage entity, CONTEXT_TYPE context) {
		timeOfEventFieldAction.performFieldAction(entity, context);
		timeOfInterceptionFieldAction.performFieldAction(entity, context);
		messageFieldAction.performFieldAction(entity, context);
		if (entity.senderLocation != null) {
			senderLocationFieldAction.performFieldAction(entity, context);
		}
		if (entity.receiverLocation != null) {
			receiverLocationFieldAction.performFieldAction(entity, context);
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

class ConversationVisitor<CONTEXT_TYPE extends VisitingContext> extends EntityVisitor<Conversation, CONTEXT_TYPE> {
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
		timeOfEventFieldAction = simpleFieldActionFactory.getFieldAction(Conversation.class, "timeOfEvent");
		timeOfInterceptionFieldAction = simpleFieldActionFactory.getFieldAction(Conversation.class, "timeOfInterception");
		durationInSecondsFieldAction = simpleFieldActionFactory.getFieldAction(Conversation.class, "durationInSeconds");
		primaryLocationFieldAction = simpleFieldActionFactory.getFieldAction(Conversation.class, "primaryLocation");
		secondaryLocationFieldAction = simpleFieldActionFactory.getFieldAction(Conversation.class, "secondaryLocation");
		primaryFieldPreAction = preFieldActionFactory.getFieldPreAction(Conversation.class, "primary");
		primaryFieldPostAction = postFieldActionFactory.getFieldPostAction(Conversation.class, "primary");
		secondaryFieldPreAction = preFieldActionFactory.getFieldPreAction(Conversation.class, "secondary");
		secondaryFieldPostAction = postFieldActionFactory.getFieldPostAction(Conversation.class, "secondary");
	}

	@Override
	public void visitEntity(Conversation entity, CONTEXT_TYPE context) {
		timeOfEventFieldAction.performFieldAction(entity, context);
		timeOfInterceptionFieldAction.performFieldAction(entity, context);
		durationInSecondsFieldAction.performFieldAction(entity, context);
		if (entity.primaryLocation != null) {
			primaryLocationFieldAction.performFieldAction(entity, context);
		}
		if (entity.secondaryLocation != null) {
			secondaryLocationFieldAction.performFieldAction(entity, context);
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

class EmailEndpointVisitor<CONTEXT_TYPE extends VisitingContext> extends EntityVisitor<EmailEndpoint, CONTEXT_TYPE> {
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

class CommunicationEndpointVisitor<CONTEXT_TYPE extends VisitingContext> extends EntityVisitor<CommunicationEndpoint, CONTEXT_TYPE> {
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

class LocalnameVisitor<CONTEXT_TYPE extends VisitingContext> extends EntityVisitor<Localname, CONTEXT_TYPE> {
	private final VisitorFieldSimpleAction<Localname, CONTEXT_TYPE> nameFieldAction;

	public LocalnameVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> fieldActionFactory) {
		super(preEntityActionFactory.getEntityPreAction(Localname.class), postEntityActionFactory.getEntityPostAction(Localname.class));
		nameFieldAction = fieldActionFactory.getFieldAction(Localname.class, "name");
	}

	@Override
	protected void visitEntity(Localname entity, CONTEXT_TYPE context) {
		nameFieldAction.performFieldAction(entity, context);
	}
}

class DisplayedNameVisitor<CONTEXT_TYPE extends VisitingContext> extends EntityVisitor<DisplayedName, CONTEXT_TYPE> {
	private final VisitorFieldSimpleAction<DisplayedName, CONTEXT_TYPE> nameFieldAction;

	public DisplayedNameVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> fieldActionFactory) {
		super(preEntityActionFactory.getEntityPreAction(DisplayedName.class), postEntityActionFactory.getEntityPostAction(DisplayedName.class));
		nameFieldAction = fieldActionFactory.getFieldAction(DisplayedName.class, "name");
	}

	@Override
	protected void visitEntity(DisplayedName entity, CONTEXT_TYPE context) {
		nameFieldAction.performFieldAction(entity, context);
	}
}

class PublicIdVisitor<CONTEXT_TYPE extends VisitingContext> extends EntityVisitor<PublicId, CONTEXT_TYPE> {
	private final VisitorFieldSimpleAction<PublicId, CONTEXT_TYPE> numberFieldAction;

	public PublicIdVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> fieldActionFactory) {
		super(preEntityActionFactory.getEntityPreAction(PublicId.class), postEntityActionFactory.getEntityPostAction(PublicId.class));
		numberFieldAction = fieldActionFactory.getFieldAction(PublicId.class, "number");
	}

	@Override
	protected void visitEntity(PublicId entity, CONTEXT_TYPE context) {
		numberFieldAction.performFieldAction(entity, context);
	}
}

class PrivateIdVisitor<CONTEXT_TYPE extends VisitingContext> extends EntityVisitor<PrivateId, CONTEXT_TYPE> {
	private final VisitorFieldSimpleAction<PrivateId, CONTEXT_TYPE> numberFieldAction;

	public PrivateIdVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> fieldActionFactory) {
		super(preEntityActionFactory.getEntityPreAction(PrivateId.class), postEntityActionFactory.getEntityPostAction(PrivateId.class));
		numberFieldAction = fieldActionFactory.getFieldAction(PrivateId.class, "number");
	}

	@Override
	protected void visitEntity(PrivateId entity, CONTEXT_TYPE context) {
		numberFieldAction.performFieldAction(entity, context);
	}
}

class DomainVisitor<CONTEXT_TYPE extends VisitingContext> extends EntityVisitor<Domain, CONTEXT_TYPE> {
	private final VisitorFieldSimpleAction<Domain, CONTEXT_TYPE> domainPartFieldAction;
	private final VisitorFieldPreAction<Domain, CONTEXT_TYPE> domainFieldPreAction;
	private final VisitorFieldPostAction<Domain, CONTEXT_TYPE> domainFieldPostAction;

	public DomainVisitor(VisitorEntityPreActionFactory<CONTEXT_TYPE> preEntityActionFactory,
			VisitorEntityPostActionFactory<CONTEXT_TYPE> postEntityActionFactory, VisitorFieldSimpleActionFactory<CONTEXT_TYPE> simpleFieldActionFactory,
			VisitorFieldPreActionFactory<CONTEXT_TYPE> preFieldActionFactory, VisitorFieldPostActionFactory<CONTEXT_TYPE> postFieldActionFactory) {
		super(preEntityActionFactory.getEntityPreAction(Domain.class), postEntityActionFactory.getEntityPostAction(Domain.class));
		domainPartFieldAction = simpleFieldActionFactory.getFieldAction(Domain.class, "domainPart");
		domainFieldPreAction = preFieldActionFactory.getFieldPreAction(Domain.class, "domain");
		domainFieldPostAction = postFieldActionFactory.getFieldPostAction(Domain.class, "domain");
	}

	@Override
	public void visitEntity(Domain entity, CONTEXT_TYPE context) {
		domainPartFieldAction.performFieldAction(entity, context);
		if (entity.domain != null) {
			domainFieldPreAction.performFieldPreAction(entity, context);
			visit(entity.domain, context);
			domainFieldPostAction.performFieldPostAction(entity, context);
		}
	}
}

class EmailAddressVisitor<CONTEXT_TYPE extends VisitingContext> extends EntityVisitor<EmailAddress, CONTEXT_TYPE> {
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
