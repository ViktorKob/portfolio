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
		VisitorFieldSimpleActionFactory<SerializerContext>, VisitorFieldPreActionFactory<SerializerContext>, VisitorFieldPostActionFactory<SerializerContext>
{

	public static class SerializerContext implements VisitingContext {
		public final JsonGenerator generator;
		public final SerializerProvider serializers;

		public SerializerContext(final JsonGenerator generator, final SerializerProvider serializers) {
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
	public <T extends Entity> VisitorEntityPreAction<T, SerializerContext> getEntityPreAction(final Class<T> entityClass) {
		if (actions.containsKey(entityClass)) {
			return (VisitorEntityPreAction<T, SerializerContext>) actions.get(entityClass);
		} else {
			return (entity, context) -> {
				throw new EntitySerializationException("Unable to serialize " + entity + ":  Action not implemented");
			};
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Entity> VisitorEntityPostAction<T, SerializerContext> getEntityPostAction(final Class<T> entityClass) {
		if (actions.containsKey(entityClass)) {
			return (VisitorEntityPostAction<T, SerializerContext>) actions.get(entityClass);
		} else {
			return (entity, context) -> {
				throw new EntitySerializationException("Unable to serialize " + entity + ":  Action not implemented");
			};
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Entity> VisitorFieldPreAction<T, SerializerContext> getFieldPreAction(final Class<T> entityClass, final String field) {
		if (actions.containsKey(entityClass)) {
			return (VisitorFieldPreAction<T, SerializerContext>) actions.get(entityClass).getFieldPreAction(field);
		} else {
			return (entity, context) -> {
				throw new EntitySerializationException("Unable to serialize " + entity + ":  Action not implemented");
			};
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Entity> VisitorFieldPostAction<T, SerializerContext> getFieldPostAction(final Class<T> entityClass, final String field) {
		if (actions.containsKey(entityClass)) {
			return (VisitorFieldPostAction<T, SerializerContext>) actions.get(entityClass).getFieldPostAction(field);
		} else {
			return (entity, context) -> {
				throw new EntitySerializationException("Unable to serialize " + entity + ":  Action not implemented");
			};
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Entity> VisitorFieldSimpleAction<T, SerializerContext> getFieldSimpleAction(final Class<T> entityClass, final String field) {
		if (actions.containsKey(entityClass)) {
			return (VisitorFieldSimpleAction<T, SerializerContext>) actions.get(entityClass).getFieldSimpleAction(field);
		} else {
			return (entity, context) -> {
				throw new EntitySerializationException("Unable to serialize " + entity + ":  Action not implemented");
			};
		}
	}

	abstract class SerializerVisitorActions<ENTITY_TYPE extends Entity>
			implements VisitorEntityPreAction<ENTITY_TYPE, SerializerContext>, VisitorEntityPostAction<ENTITY_TYPE, SerializerContext>
	{
		protected final VisitorFieldSimpleAction<ENTITY_TYPE, SerializerContext> NO_SIMPLE_ACTION = (entity, context) -> {
			// Not applicable here
		};
		protected final VisitorFieldPreAction<ENTITY_TYPE, SerializerContext> NO_FIELD_PRE_ACTION = (entity, context) -> {
			// Not applicable here
		};
		protected final VisitorFieldPostAction<ENTITY_TYPE, SerializerContext> NO_FIELD_POST_ACTION = (entity, context) -> {
			// Not applicable here
		};

		// TODO[Thomas]: Replace with annotated aspect instead
		@Override
		public void performEntityPreAction(final ENTITY_TYPE entity, final SerializerContext context) {
			try {
				final JsonGenerator generator = getContext(context).generator;
				generator.writeStartObject();
				generator.writeStringField("id", entity.uid);
				_performPreEntityAction(entity, getContext(context));
			} catch (final IOException e) {
				throw new EntitySerializationException("Unable to serialize " + entity.toString(), e);
			}
		}

		protected void _performPreEntityAction(final ENTITY_TYPE entity, final SerializerContext context) throws IOException {
			// Not applicable here
		}

		// TODO[Thomas]: Replace with annotated aspect instead
		@Override
		public void performEntityPostAction(final ENTITY_TYPE entity, final SerializerContext context) {
			try {
				_performPostEntityAction(entity, getContext(context));
				final JsonGenerator generator = getContext(context).generator;
				generator.writeEndObject();
			} catch (final IOException e) {
				throw new EntitySerializationException("Unable to serialize " + entity.toString(), e);
			}
		}

		protected SerializerContext getContext(final SerializerContext context) {
			return context;
		}

		protected void _performPostEntityAction(final ENTITY_TYPE entity, final SerializerContext context) throws IOException {
			// Not applicable here
		}

		public VisitorFieldPreAction<ENTITY_TYPE, SerializerContext> getFieldPreAction(final String field) {
			return NO_FIELD_PRE_ACTION;
		}

		public VisitorFieldPostAction<ENTITY_TYPE, SerializerContext> getFieldPostAction(final String field) {
			return NO_FIELD_POST_ACTION;
		}

		public VisitorFieldSimpleAction<ENTITY_TYPE, SerializerContext> getFieldSimpleAction(final String field) {
			return NO_SIMPLE_ACTION;
		}

		protected String typeOf(final Entity entity) {
			return entity.getClass().getSimpleName();
		}

		protected void writeTimestamp(final JsonGenerator generator, final Timestamp timestamp) throws IOException {
			generator.writeStartObject();
			generator.writeNumberField("t", timestamp.getTimestamp());
			generator.writeStringField("z", timestamp.getOriginalTimeZoneId());
			generator.writeEndObject();
		}

		protected void writeLocation(final JsonGenerator generator, final GeoLocation location) throws IOException {
			generator.writeStartObject();
			generator.writeNumberField("x", location.getLongitude());
			generator.writeNumberField("y", location.getLatitude());
			generator.writeEndObject();
		}

		protected VisitorFieldPreAction<ENTITY_TYPE, SerializerContext> wrapPreWithSerializerAction(final Action<ENTITY_TYPE> innerAction) {
			final SerializerAction<ENTITY_TYPE> action = new SerializerAction<>(innerAction);
			return (entity, context) -> {
				action.perform(entity, context);
			};
		}

		protected VisitorFieldPostAction<ENTITY_TYPE, SerializerContext> wrapPostWithSerializerAction(final Action<ENTITY_TYPE> innerAction) {
			final SerializerAction<ENTITY_TYPE> action = new SerializerAction<>(innerAction);
			return (entity, context) -> {
				action.perform(entity, context);
			};
		}

		protected VisitorFieldSimpleAction<ENTITY_TYPE, SerializerContext> wrapSimpleWithSerializerAction(final Action<ENTITY_TYPE> innerAction) {
			final SerializerAction<ENTITY_TYPE> action = new SerializerAction<>(innerAction);
			return (entity, context) -> {
				action.perform(entity, context);
			};
		}
	}

	class EmailVisitorActions extends SerializerVisitorActions<Email> {
		@Override
		public VisitorFieldSimpleAction<Email, SerializerContext> getFieldSimpleAction(final String field) {
			if ("subject".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeStringField("s", entity.subject);
				});
			} else if ("timeOfEvent".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("tOE");
					writeTimestamp(generator, entity.timeOfEvent);
				});
			} else if ("timeOfInterception".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("tOI");
					writeTimestamp(generator, entity.timeOfInterception);
				});
			} else if ("message".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeStringField("m", entity.message);
				});
			} else {
				return NO_SIMPLE_ACTION;
			}
		}

		@Override
		public VisitorFieldPreAction<Email, SerializerContext> getFieldPreAction(final String field) {
			if ("from".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("a");
				});
			} else if ("to".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("b");
					generator.writeStartArray();
				});
			} else if ("cc".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("c");
					generator.writeStartArray();
				});
			} else if ("bcc".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("d");
					generator.writeStartArray();
				});
			} else {
				return NO_FIELD_PRE_ACTION;
			}
		}

		@Override
		public VisitorFieldPostAction<Email, SerializerContext> getFieldPostAction(final String field) {
			if ("to".equals(field)) {
				return wrapPostWithSerializerAction((entity, generator) -> {
					generator.writeEndArray();
				});
			} else if ("cc".equals(field)) {
				return wrapPostWithSerializerAction((entity, generator) -> {
					generator.writeEndArray();
				});
			} else if ("bcc".equals(field)) {
				return wrapPostWithSerializerAction((entity, generator) -> {
					generator.writeEndArray();
				});
			} else {
				return NO_FIELD_POST_ACTION;
			}
		}

		@Override
		protected void _performPreEntityAction(final Email entity, final SerializerContext context) throws IOException {
			context.generator.writeFieldName("t");
			context.generator.writeObject(typeOf(entity));
		}
	}

	class TextMessageVisitorActions extends SerializerVisitorActions<TextMessage> {
		@Override
		public VisitorFieldSimpleAction<TextMessage, SerializerContext> getFieldSimpleAction(final String field) {
			if ("message".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeStringField("m", entity.message);
				});
			} else if ("timeOfEvent".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("tOE");
					writeTimestamp(generator, entity.timeOfEvent);
				});
			} else if ("timeOfInterception".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("tOI");
					writeTimestamp(generator, entity.timeOfInterception);
				});
			} else if ("senderLocation".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("aL");
					writeLocation(generator, entity.senderLocation);
				});
			} else if ("receiverLocation".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("bL");
					writeLocation(generator, entity.receiverLocation);
				});
			} else {
				return NO_SIMPLE_ACTION;
			}
		}

		@Override
		public VisitorFieldPreAction<TextMessage, SerializerContext> getFieldPreAction(final String field) {
			if ("sender".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("a");
				});
			} else if ("receiver".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("b");
				});
			} else {
				return NO_FIELD_PRE_ACTION;
			}
		}

		@Override
		protected void _performPreEntityAction(final TextMessage entity, final SerializerContext context) throws IOException {
			context.generator.writeFieldName("t");
			context.generator.writeObject(typeOf(entity));
		}
	}

	class ConversationVisitorActions extends SerializerVisitorActions<Conversation> {
		@Override
		public VisitorFieldSimpleAction<Conversation, SerializerContext> getFieldSimpleAction(final String field) {
			if ("durationInSeconds".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeNumberField("d", entity.durationInSeconds);
				});
			} else if ("timeOfEvent".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("tOE");
					writeTimestamp(generator, entity.timeOfEvent);
				});
			} else if ("timeOfInterception".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("tOI");
					writeTimestamp(generator, entity.timeOfInterception);
				});
			} else if ("primaryLocation".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("aL");
					writeLocation(generator, entity.primaryLocation);
				});
			} else if ("secondaryLocation".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("bL");
					writeLocation(generator, entity.secondaryLocation);
				});
			} else {
				return NO_SIMPLE_ACTION;
			}
		}

		@Override
		public VisitorFieldPreAction<Conversation, SerializerContext> getFieldPreAction(final String field) {
			if ("primary".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("a");
				});
			} else if ("secondary".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("b");
				});
			} else {
				return NO_FIELD_PRE_ACTION;
			}
		}

		@Override
		protected void _performPreEntityAction(final Conversation entity, final SerializerContext context) throws IOException {
			context.generator.writeFieldName("t");
			context.generator.writeObject(typeOf(entity));
		}
	}

	class LocalnameVisitorActions extends SerializerVisitorActions<Localname> {
		@Override
		public VisitorFieldSimpleAction<Localname, SerializerContext> getFieldSimpleAction(final String field) {
			if ("name".equals(field)) {
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
		public VisitorFieldSimpleAction<DisplayedName, SerializerContext> getFieldSimpleAction(final String field) {
			if ("name".equals(field)) {
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
		public VisitorFieldSimpleAction<PublicId, SerializerContext> getFieldSimpleAction(final String field) {
			if ("number".equals(field)) {
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
		public VisitorFieldSimpleAction<PrivateId, SerializerContext> getFieldSimpleAction(final String field) {
			if ("number".equals(field)) {
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
		public VisitorFieldSimpleAction<Domain, SerializerContext> getFieldSimpleAction(final String field) {
			if ("domainPart".equals(field)) {
				return wrapSimpleWithSerializerAction((entity, generator) -> {
					generator.writeStringField("dP", entity.domainPart);
				});
			} else {
				return NO_SIMPLE_ACTION;
			}
		}

		@Override
		public VisitorFieldPreAction<Domain, SerializerContext> getFieldPreAction(final String field) {
			if ("domain".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("d");
				});
			} else {
				return NO_FIELD_PRE_ACTION;
			}
		}
	}

	class EmailAddressVisitorActions extends SerializerVisitorActions<EmailAddress> {
		@Override
		public VisitorFieldPreAction<EmailAddress, SerializerContext> getFieldPreAction(final String field) {
			if ("localname".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("l");
				});
			} else if ("domain".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("d");
				});
			} else {
				return NO_FIELD_PRE_ACTION;
			}
		}
	}

	class EmailEndpointVisitorActions extends SerializerVisitorActions<EmailEndpoint> {
		@Override
		public VisitorFieldPreAction<EmailEndpoint, SerializerContext> getFieldPreAction(final String field) {
			if ("displayedName".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("d");
				});
			} else if ("address".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("a");
				});
			} else {
				return NO_FIELD_PRE_ACTION;
			}
		}
	}

	class CommunicationEndpointVisitorActions extends SerializerVisitorActions<CommunicationEndpoint> {
		@Override
		public VisitorFieldPreAction<CommunicationEndpoint, SerializerContext> getFieldPreAction(final String field) {
			if ("publicId".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("a");
				});
			} else if ("privateId".equals(field)) {
				return wrapPreWithSerializerAction((entity, generator) -> {
					generator.writeFieldName("b");
				});
			} else {
				return NO_FIELD_PRE_ACTION;
			}
		}
	}

	static class SerializerAction<ENTITY_TYPE extends Entity> {
		private final Action<ENTITY_TYPE> action;

		public SerializerAction(final Action<ENTITY_TYPE> action) {
			this.action = action;
		}

		public void perform(final ENTITY_TYPE entity, final SerializerContext context) {
			final JsonGenerator generator = context.generator;
			try {
				action.perform(entity, generator);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FunctionalInterface
	interface Action<ENTITY_TYPE extends Entity> {
		void perform(ENTITY_TYPE entity, JsonGenerator generator) throws IOException;
	}

	public static class EntitySerializationException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public EntitySerializationException(String message) {
			super(message);
		}

		public EntitySerializationException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
