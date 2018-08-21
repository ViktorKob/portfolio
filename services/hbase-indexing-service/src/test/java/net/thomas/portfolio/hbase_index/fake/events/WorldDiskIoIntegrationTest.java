package net.thomas.portfolio.hbase_index.fake.events;

import static java.nio.file.Paths.get;
import static org.assertj.core.util.Files.delete;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.fake.FakeWorldStorage;
import net.thomas.portfolio.hbase_index.fake.generators.FakeWorldGenerator;
import net.thomas.portfolio.hbase_index.fake.world.storage.EventDiskReader;
import net.thomas.portfolio.hbase_index.fake.world.storage.EventDiskWriter;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;

public class WorldDiskIoIntegrationTest {
	private static final long RANDOM_SEED = 1234L;
	private static final int POPULATION = 5;
	private static final int AVG_SOCIAL_CONNECTIONS = 2;
	private static final int AVG_COMMUNICATION = 5;
	private static final String STORAGE_ROOT = "test_data";
	private static final String STORAGE_ROOT_THAT_DOES_NOT_EXIST = "fake_test_data";
	private static final String UID_THAT_DOES_NOT_EXIST = "AAAAAAAA0000";
	private static EventDiskWriter writer;
	private static EventDiskReader reader;
	private static FakeWorldStorage storage;

	@BeforeClass
	public static void setUpWorld() {
		delete(get(STORAGE_ROOT_THAT_DOES_NOT_EXIST).toFile());
		delete(get(STORAGE_ROOT).toFile());
		storage = new FakeWorldStorage();
		writer = new EventDiskWriter(STORAGE_ROOT);
		new FakeWorldGenerator(RANDOM_SEED, POPULATION, AVG_SOCIAL_CONNECTIONS, AVG_COMMUNICATION)
				.generateAndWrite(storage);
		new FakeWorldGenerator(RANDOM_SEED, POPULATION, AVG_SOCIAL_CONNECTIONS, AVG_COMMUNICATION)
				.generateAndWrite(writer);
	}

	@Before
	public void setUpForTest() {
		reader = new EventDiskReader(STORAGE_ROOT);
	}

	@Test
	public void shouldNotBeAbleToReadNonExistantRoot() {
		assertFalse(new EventDiskReader(STORAGE_ROOT_THAT_DOES_NOT_EXIST).canRead());
	}

	@Test
	public void shouldHaveAllEventsFromWorldOnDisk() {
		for (Event event : storage) {
			assertEquals(event, reader.getEvent(event.uid));
		}
	}

	@Test
	public void shouldReturnNullForNonExistantEvent() {
		assertNull(reader.getEvent(UID_THAT_DOES_NOT_EXIST));
	}

	@Test
	public void shouldHaveCorrectReferenceForEachEventInMemory() {
		for (Event event : storage) {
			assertEquals(storage.getReferences(event.uid), reader.getReferences(event.uid));
		}
	}

	@Test
	public void shouldHaveCorrectReferenceForEachEventOnDisk() {
		for (Event event : reader) {
			assertEquals(storage.getReferences(event.uid), reader.getReferences(event.uid));
		}
	}

	@Test
	public void shouldReturnEmptyReferencesForNonExistantReferencesForNonExistantEvent() {
		References references = reader.getReferences(UID_THAT_DOES_NOT_EXIST);
		assertFalse(references.hasData());
	}

	@Test
	public void shouldIterateThroughAllEventsFromWorld() {
		Set<String> foundEvents = new HashSet<>();
		for (Event event : reader) {
			foundEvents.add(event.uid);
		}
		for (Event event : storage) {
			assertTrue(foundEvents.contains(event.uid));
		}
	}

	@Test
	public void shouldNotContainEventsNotInWorld() {
		Set<String> foundEvents = new HashSet<>();
		for (Event event : storage) {
			foundEvents.add(event.uid);
		}
		for (Event event : reader) {
			assertTrue(foundEvents.contains(event.uid));
		}
	}

	@Test(expected = NoSuchElementException.class)
	public void shouldThrowExceptionWhenteratingBeyondLastElement() {
		Iterator<Event> iterator = reader.iterator();
		while (iterator.hasNext()) {
			iterator.next();
		}
		iterator.next();
	}

	@AfterClass
	public static void cleanupDisk() {
		delete(get(STORAGE_ROOT).toFile());
	}
}