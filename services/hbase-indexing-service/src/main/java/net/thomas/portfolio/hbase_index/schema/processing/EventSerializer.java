package net.thomas.portfolio.hbase_index.schema.processing;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.processing.EntitySerializerActionFactory.SerializerContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.StrictEntityHierarchyVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.StrictEntityHierarchyVisitorBuilder;

@ThreadSafe
public class EventSerializer extends JsonSerializer<Event> {

	private static final StrictEntityHierarchyVisitor<SerializerContext> SERIALIZATION_VISITOR;
	static {
		final EntitySerializerActionFactory serializerFactory = new EntitySerializerActionFactory();
		SERIALIZATION_VISITOR = new StrictEntityHierarchyVisitorBuilder<SerializerContext>().setEntityPreActionFactory(serializerFactory)
			.setEntityPostActionFactory(serializerFactory)
			.setFieldSimpleActionFactory(serializerFactory)
			.setFieldPreActionFactory(serializerFactory)
			.setFieldPostActionFactory(serializerFactory)
			.build();
	}

	@Override
	public void serialize(Event event, JsonGenerator generator, SerializerProvider serializers) throws IOException {
		SERIALIZATION_VISITOR.visit(event, new SerializerContext(generator, serializers));
	}
}