package net.thomas.portfolio.hbase_index.fake.world;

import static java.nio.file.Paths.get;
import static org.assertj.core.util.Files.delete;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.fake.FakeWorld;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;

public class WorldIoControlIntegrationTest {
	private static final long RANDOM_SEED = 1234L;
	private static final int POPULATION = 5;
	private static final int AVG_SOCIAL_CONNECTIONS = 2;
	private static final int AVG_COMMUNICATION = 5;
	private static final String STORAGE_ROOT = "testData";
	private static final String STORAGE_ROOT_THAT_DOES_NOT_EXIST = "fakeTestData";
	private static final String UID_THAT_DOES_NOT_EXIST = "AAAAAAAA0000";
	private static FakeWorld WORLD;
	private static WorldIoControl ioControl;
	private WorldAccess worldAccess;

	@BeforeClass
	public static void setUpWorld() {
		WORLD = new FakeWorld(RANDOM_SEED, POPULATION, AVG_SOCIAL_CONNECTIONS, AVG_COMMUNICATION);
		delete(get(STORAGE_ROOT_THAT_DOES_NOT_EXIST).toFile());
		delete(get(STORAGE_ROOT).toFile());
		ioControl = new WorldIoControl(STORAGE_ROOT);
		ioControl.exportWorld(WORLD);
	}

	@Before
	public void setUpForTest() {
		worldAccess = ioControl.getWorldAccess();
	}

	@Test
	public void shouldNotBeAbleToImportNonExistantRoot() {
		assertFalse(new WorldIoControl(STORAGE_ROOT_THAT_DOES_NOT_EXIST).canImportWorld());
	}

	@Test
	public void shouldHaveAllEventsFromWorldOnDisk() {
		for (Event event : WORLD.getEvents()) {
			assertEquals(event, worldAccess.getEvent(event.uid));
		}
	}

	@Test
	public void shouldReturnNullForNonExistantEvent() {
		assertNull(worldAccess.getEvent(UID_THAT_DOES_NOT_EXIST));
	}

	@Test
	public void shouldHaveCorrectReferenceForEachEventOnDisk() {
		for (Event event : WORLD.getEvents()) {
			assertEquals(WORLD.getSourceReferences().get(event.uid), worldAccess.getReferences(event.uid));
		}
	}

	@Test
	public void shouldReturnEmptyReferencesForNonExistantReferencesForNonExistantEvent() {
		Collection<Reference> references = worldAccess.getReferences(UID_THAT_DOES_NOT_EXIST).getReferences();
		assertTrue(references.isEmpty());
	}

	@Test
	public void shouldIterateThroughAllEventsFromWorld() {
		Set<String> foundEvents = new HashSet<>();
		for (Event event : worldAccess) {
			foundEvents.add(event.uid);
		}
		for (Event event : WORLD.getEvents()) {
			assertTrue(foundEvents.contains(event.uid));
		}
	}

	@Test(expected = NoSuchElementException.class)
	public void shouldThrowExceptionWhenteratingBeyondLastElement() {
		Iterator<Event> iterator = worldAccess.iterator();
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