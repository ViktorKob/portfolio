package net.thomas.portfolio.hbase_index.schema;

import static java.lang.System.nanoTime;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.thomas.portfolio.hbase_index.fake.FakeWorld;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.processing.EventDeserializer;
import net.thomas.portfolio.hbase_index.schema.processing.EventSerializer;

public class EventProtocolUnitTest {
	private static Collection<Event> events;
	private static ObjectMapper mapper;

	@BeforeClass
	public static void createWorld() {
		events = new FakeWorld(nanoTime(), 3, 3, 50).getEvents();
		mapper = setupMapper();
	}

	private static ObjectMapper setupMapper() {
		final ObjectMapper visitorBasedMapper = new ObjectMapper();
		final SimpleModule module = new SimpleModule();
		module.addSerializer(Event.class, new EventSerializer());
		module.addDeserializer(Event.class, new EventDeserializer());
		visitorBasedMapper.registerModule(module);
		return visitorBasedMapper;
	}

	@Test
	public void shouldAllBeIdenticalAfterSerializationDeserialization() throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		for (final Event event : events) {
			final Event convertedEvent = mapper.readValue(mapper.writeValueAsString(event), Event.class);
			assertEquals(event, convertedEvent);
		}
	}
}
