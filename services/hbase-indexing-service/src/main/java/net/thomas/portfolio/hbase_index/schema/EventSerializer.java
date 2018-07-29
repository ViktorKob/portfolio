package net.thomas.portfolio.hbase_index.schema;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import net.thomas.portfolio.hbase_index.schema.EntitySerializerActionFactory.SerializerContext;
import net.thomas.portfolio.hbase_index.schema.documents.Event;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.EntityHierarchyVisitorBuilder;

@ThreadSafe
public class EventSerializer extends JsonSerializer<Event> {

	private static final EntityHierarchyVisitor<SerializerContext> SERIALIZATION_VISITOR;
	static {
		final EntitySerializerActionFactory serializerFactory = new EntitySerializerActionFactory();
		SERIALIZATION_VISITOR = new EntityHierarchyVisitorBuilder<SerializerContext>().setEntityPreActionFactory(serializerFactory)
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