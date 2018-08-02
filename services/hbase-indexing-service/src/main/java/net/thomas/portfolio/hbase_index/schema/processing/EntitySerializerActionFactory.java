package net.thomas.portfolio.hbase_index.schema.processing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.events.Conversation;
import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.events.TextMessage;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.hbase_index.schema.processing.EntitySerializerActionFactory.SerializerContext;
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
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;
import net.thomas.portfolio.hbase_index.schema.selectors.Domain;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;
import net.thomas.portfolio.hbase_index.schema.selectors.PrivateId;
import net.thomas.portfolio.hbase_index.schema.selectors.PublicId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class EntitySerializerActionFactory implements VisitorEntityPreActionFactory<SerializerContext>, VisitorEntityPostActionFactory<SerializerContext>,
		VisitorFieldSimpleActionFactory<SerializerContext>, VisitorFieldPreActionFactory<SerializerContext>, VisitorFieldPostActionFactory<SerializerContext> {

	public static class SerializerContext implements VisitingContext {
		public final JsonGenerator generator;
		public final SerializerProvider serializers;

		public SerializerContext(JsonGenerator generator, SerializerProvider serializers) {
			this.generator = generator;
			this.serializers = serializers;
		}
	}

	private final Map<Class<? extends Entity>, SerializerVisitorActions<? extends Entity>> actions;

	public EntitySerializerActionFactory() {
		actions = new HashMap<>();
		actions.put(CommunicationEndpoint.class, new CommunicationEndpointVisitorActions());
		actions.put(Conversation.class, new ConversationVisitorActions());
		actions.put(DisplayedName.class, new DisplayedNameVisitorActions());
		actions.put(Domain.class, new DomainVisitorActions());
		actions.put(Email.class, new EmailVisitorActions());
		actions.put(EmailAddress.class, new EmailAddressVisitorActions());
		actions.put(EmailEndpoint.class, new EmailEndpointVisitorActions());
		actions.put(Localname.class, new LocalnameVisitorActions());
		actions.put(PublicId.class, new PublicIdVisitorActions());
		actions.put(PrivateId.class, new PrivateIdVisitorActions());
		actions.put(TextMessage.class, new TextMessageVisitorActions());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Entity> VisitorEntityPreAction<T, SerializerContext> getEntityPreAction(Class<T> entityClass) {
		if (actions.containsKey(entityClass)) {
			return (VisitorEntityPreAction<T, SerializerContext>) actions.get(entityClass);
		} else {
			return (entity, context) -> {
				throw new RuntimeException("Unable to serialize " + entity);
			};
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Entity> VisitorEntityPostAction<T, SerializerContext> getEntityPostAction(Class<T> entityClass) {
		if (actions.containsKey(entityClass)) {
			return (VisitorEntityPostAction<T, SerializerContext>) actions.get(entityClass);
		} else {
			return (entity, context) -> {
				throw new RuntimeException("Unable to serialize " + entity);
			};
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Entity> VisitorFieldPreAction<T, SerializerContext> getFieldPreAction(Class<T> entityClass, String field) {
		if (actions.containsKey(entityClass)) {
			return (VisitorFieldPreAction<T, SerializerContext>) actions.get(entityClass)
				.getFieldPreAction(field);
		} else {
			return (entity, context) -> {
				throw new RuntimeException("Unable to serialize " + entity);
			};
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Entity> VisitorFieldPostAction<T, SerializerContext> getFieldPostAction(Class<T> entityClass, String field) {
		if (actions.containsKey(entityClass)) {
			return (VisitorFieldPostAction<T, SerializerContext>) actions.get(entityClass)
				.getFieldPostAction(field);
		} else {
			return (entity, context) -> {
				throw new RuntimeException("Unable to serialize " + entity);
			};
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Entity> VisitorFieldSimpleAction<T, SerializerContext> getSimpleFieldAction(Class<T> entityClass, String field) {
		if (actions.containsKey(entityClass)) {
			return (VisitorFieldSimpleAction<T, SerializerContext>) actions.get(entityClass)
				.getFieldSimpleAction(field);
		} else {
			return (entity, context) -> {
				throw new RuntimeException("Unable to serialize " + entity);
			};
		}
	}

	abstract class SerializerVisitorActions<ENTITY_TYPE extends Entity>
			implements VisitorEntityPreAction<ENTITY_TYPE, SerializerContext>, VisitorEntityPostAction<ENTITY_TYPE, SerializerContext> {
		protected final VisitorFieldSimpleAction<ENTITY_TYPE, SerializerContext> NO_SIMPLE_ACTION = (entity, context) -> {
		};
		protected final VisitorFieldPreAction<ENTITY_TYPE, SerializerContext> DEFAULT_PRE_ACTION = (entity, context) -> {
		};
		protected final VisitorFieldPostAction<ENTITY_TYPE, SerializerContext> DEFAULT_POST_ACTION = (entity, context) -> {
		};

		// TODO[Thomas]: Replace with annotated aspect instead
		@Override
		public void performEntityPreAction(ENTITY_TYPE entity, SerializerContext context) {
			try {
				final JsonGenerator generator = getContext(context).generator;
				generator.writeStartObject();
				generator.writeStringField("id", entity.uid);
				_performPreEntityAction(entity, getContext(context));
			} catch (final IOException e) {
				throw new RuntimeException("Unable to serialize " + entity.toString(), e);
			}
		}

		protected void _performPreEntityAction(ENTITY_TYPE entity, SerializerContext context) throws IOException {
		}

		// TODO[Thomas]: Replace with annotated aspect instead
		@Override
		public void performEntityPostAction(ENTITY_TYPE entity, SerializerContext context) {
			try {
				_performPostEntityAction(entity, getContext(context));
				final JsonGenerator generator = getContext(context).generator;
				generator.writeEndObject();
			} catch (final IOException e) {
				throw new RuntimeException("Unable to serialize " + entity.toString(), e);
			}
		}

		protected SerializerContext getContext(SerializerContext context) {
			return context;
		}

		protected void _performPostEntityAction(ENTITY_TYPE entity, SerializerContext context) throws IOException {
		}

		public VisitorFieldPreAction<ENTITY_TYPE, SerializerContext> getFieldPreAction(String field) {
			return DEFAULT_PRE_ACTION;
		}

		public VisitorFieldPostAction<ENTITY_TYPE, SerializerContext> getFieldPostAction(String field) {
			return DEFAULT_POST_ACTION;
		}

		public VisitorFieldSimpleAction<ENTITY_TYPE, SerializerContext> getFieldSimpleAction(String field) {
			return NO_SIMPLE_ACTION;
		}

		protected String typeOf(Entity entity) {
			return entity.getClass()
				.getSimpleName();
		}

		protected void writeTimestamp(JsonGenerator generator, Timestamp timestamp) throws IOException {
			generator.writeStartObject();
			generator.writeNumberField("t", timestamp.getTimestamp());
			generator.writeStringField("z", timestamp.getOriginalTimeZoneId());
			generator.writeEndObject();
		}

		protected void writeLocation(JsonGenerator generator, GeoLocation location) throws IOException {
			generator.writeStartObject();
			generator.writeNumberField("x", location.getLongitude());
			generator.writeNumberField("y", location.getLatitude());
			generator.writeEndObject();
		}

		protected VisitorFieldPreAction<ENTITY_TYPE, SerializerContext> wrapPreWithSerializerAction(Action<ENTITY_TYPE> innerAction) {
			final SerializerAction<ENTITY_TYPE> action = new SerializerAction<>(innerAction);
			return (entity, context) -> {
				action.perform(entity, context);
			};
		}

		protected VisitorFieldPostAction<ENTITY_TYPE, SerializerContext> wrapPostWithSerializerAction(Action<ENTITY_TYPE> innerAction) {
			final SerializerAction<ENTITY_TYPE> action = new SerializerAction<>(innerAction);
			return (entity, context) -> {
				action.perform(entity, context);
			};
		}

		protected VisitorFieldSimpleAction<ENTITY_TYPE, SerializerContext> wrapSimpleWithSerializerAction(Action<ENTITY_TYPE> innerAction) {
			final SerializerAction<ENTITY_TYPE> action = new SerializerAction<>(innerAction);
			return (entity, context) -> {
				action.perform(entity, context);
			};
		}
	}

	class EmailVisitorActions extends SerializerVisitorActions<Email> {
		@Override
		public VisitorFieldSimpleAction<Email, SerializerContext> getFieldSimpleAction(String field) {
			if (field.equals("subject")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeStringField("s", entity.subject);
				});
			} else if (field.equals("timeOfEvent")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("tOE");
					writeTimestamp(generator, entity.timeOfEvent);
				});
			} else if (field.equals("timeOfInterception")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("tOI");
					writeTimestamp(generator, entity.timeOfInterception);
				});
			} else if (field.equals("message")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeStringField("m", entity.message);
				});
			} else {
				return NO_SIMPLE_ACTION;
			}
		}

		@Override
		public VisitorFieldPreAction<Email, SerializerContext> getFieldPreAction(String field) {
			if (field.equals("from")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("a");
				});
			} else if (field.equals("to")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("b");
					generator.writeStartArray();
				});
			} else if (field.equals("cc")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("c");
					generator.writeStartArray();
				});
			} else if (field.equals("bcc")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("d");
					generator.writeStartArray();
				});
			} else {
				return DEFAULT_PRE_ACTION;
			}
		}

		@Override
		public VisitorFieldPostAction<Email, SerializerContext> getFieldPostAction(String field) {
			if (field.equals("to")) {
				return wrapPostWithSerializerAction((entity, generator) -> {
					generator.writeEndArray();
				});
			} else if (field.equals("cc")) {
				return wrapPostWithSerializerAction((entity, generator) -> {
					generator.writeEndArray();
				});
			} else if (field.equals("bcc")) {
				return wrapPostWithSerializerAction((entity, generator) -> {
					generator.writeEndArray();
				});
			} else {
				return DEFAULT_POST_ACTION;
			}
		}

		@Override
		protected void _performPreEntityAction(Email entity, SerializerContext context) throws IOException {
			context.generator.writeFieldName("t");
			context.generator.writeObject(typeOf(entity));
		}
	}

	class TextMessageVisitorActions extends SerializerVisitorActions<TextMessage> {
		@Override
		public VisitorFieldSimpleAction<TextMessage, SerializerContext> getFieldSimpleAction(String field) {
			if (field.equals("message")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeStringField("m", entity.message);
				});
			} else if (field.equals("timeOfEvent")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("tOE");
					writeTimestamp(generator, entity.timeOfEvent);
				});
			} else if (field.equals("timeOfInterception")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("tOI");
					writeTimestamp(generator, entity.timeOfInterception);
				});
			} else if (field.equals("senderLocation")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("aL");
					writeLocation(generator, entity.senderLocation);
				});
			} else if (field.equals("receiverLocation")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("bL");
					writeLocation(generator, entity.receiverLocation);
				});
			} else {
				return NO_SIMPLE_ACTION;
			}
		}

		@Override
		public VisitorFieldPreAction<TextMessage, SerializerContext> getFieldPreAction(String field) {
			if (field.equals("sender")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("a");
				});
			} else if (field.equals("receiver")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("b");
				});
			} else {
				return DEFAULT_PRE_ACTION;
			}
		}

		@Override
		protected void _performPreEntityAction(TextMessage entity, SerializerContext context) throws IOException {
			context.generator.writeFieldName("t");
			context.generator.writeObject(typeOf(entity));
		}
	}

	class ConversationVisitorActions extends SerializerVisitorActions<Conversation> {
		@Override
		public VisitorFieldSimpleAction<Conversation, SerializerContext> getFieldSimpleAction(String field) {
			if (field.equals("durationInSeconds")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeNumberField("d", entity.durationInSeconds);
				});
			} else if (field.equals("timeOfEvent")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("tOE");
					writeTimestamp(generator, entity.timeOfEvent);
				});
			} else if (field.equals("timeOfInterception")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("tOI");
					writeTimestamp(generator, entity.timeOfInterception);
				});
			} else if (field.equals("primaryLocation")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("aL");
					writeLocation(generator, entity.primaryLocation);
				});
			} else if (field.equals("secondaryLocation")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("bL");
					writeLocation(generator, entity.secondaryLocation);
				});
			} else {
				return NO_SIMPLE_ACTION;
			}
		}

		@Override
		public VisitorFieldPreAction<Conversation, SerializerContext> getFieldPreAction(String field) {
			if (field.equals("primary")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("a");
				});
			} else if (field.equals("secondary")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("b");
				});
			} else {
				return DEFAULT_PRE_ACTION;
			}
		}

		@Override
		protected void _performPreEntityAction(Conversation entity, SerializerContext context) throws IOException {
			context.generator.writeFieldName("t");
			context.generator.writeObject(typeOf(entity));
		}
	}

	class LocalnameVisitorActions extends SerializerVisitorActions<Localname> {
		@Override
		public VisitorFieldSimpleAction<Localname, SerializerContext> getFieldSimpleAction(String field) {
			if (field.equals("name")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeStringField("n", entity.name);
				});
			} else {
				return NO_SIMPLE_ACTION;
			}
		}
	}

	class DisplayedNameVisitorActions extends SerializerVisitorActions<DisplayedName> {
		@Override
		public VisitorFieldSimpleAction<DisplayedName, SerializerContext> getFieldSimpleAction(String field) {
			if (field.equals("name")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeStringField("n", entity.name);
				});
			} else {
				return NO_SIMPLE_ACTION;
			}
		}
	}

	class PublicIdVisitorActions extends SerializerVisitorActions<PublicId> {
		@Override
		public VisitorFieldSimpleAction<PublicId, SerializerContext> getFieldSimpleAction(String field) {
			if (field.equals("number")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeStringField("n", entity.number);
				});
			} else {
				return NO_SIMPLE_ACTION;
			}
		}
	}

	class PrivateIdVisitorActions extends SerializerVisitorActions<PrivateId> {
		@Override
		public VisitorFieldSimpleAction<PrivateId, SerializerContext> getFieldSimpleAction(String field) {
			if (field.equals("number")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeStringField("n", entity.number);
				});
			} else {
				return NO_SIMPLE_ACTION;
			}
		}
	}

	class DomainVisitorActions extends SerializerVisitorActions<Domain> {
		@Override
		public VisitorFieldSimpleAction<Domain, SerializerContext> getFieldSimpleAction(String field) {
			if (field.equals("domainPart")) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeStringField("dP", entity.domainPart);
				});
			} else {
				return NO_SIMPLE_ACTION;
			}
		}

		@Override
		public VisitorFieldPreAction<Domain, SerializerContext> getFieldPreAction(String field) {
			if (field.equals("domain")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("d");
				});
			} else {
				return DEFAULT_PRE_ACTION;
			}
		}
	}

	class EmailAddressVisitorActions extends SerializerVisitorActions<EmailAddress> {
		@Override
		public VisitorFieldPreAction<EmailAddress, SerializerContext> getFieldPreAction(String field) {
			if (field.equals("localname")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("l");
				});
			} else if (field.equals("domain")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("d");
				});
			} else {
				return DEFAULT_PRE_ACTION;
			}
		}
	}

	class EmailEndpointVisitorActions extends SerializerVisitorActions<EmailEndpoint> {
		@Override
		public VisitorFieldPreAction<EmailEndpoint, SerializerContext> getFieldPreAction(String field) {
			if (field.equals("displayedName")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("d");
				});
			} else if (field.equals("address")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("a");
				});
			} else {
				return DEFAULT_PRE_ACTION;
			}
		}
	}

	class CommunicationEndpointVisitorActions extends SerializerVisitorActions<CommunicationEndpoint> {
		@Override
		public VisitorFieldPreAction<CommunicationEndpoint, SerializerContext> getFieldPreAction(String field) {
			if (field.equals("publicId")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("a");
				});
			} else if (field.equals("privateId")) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("b");
				});
			} else {
				return DEFAULT_PRE_ACTION;
			}
		}
	}

	static class SerializerAction<ENTITY_TYPE extends Entity> {
		private final Action<ENTITY_TYPE> action;

		public SerializerAction(Action<ENTITY_TYPE> action) {
			this.action = action;
		}

		void perform(ENTITY_TYPE entity, SerializerContext context) {
			final JsonGenerator generator = context.generator;
			try {
				action.perform(entity, generator);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FunctionalInterface
	static interface Action<ENTITY_TYPE extends Entity> {
		void perform(ENTITY_TYPE entity, JsonGenerator generator) throws IOException;
	}
}
