package net.thomas.portfolio.hbase_index.fake.world.storage;

import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;

import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.processing.EventDeserializer;
import net.thomas.portfolio.hbase_index.schema.processing.EventSerializer;

public class EventDiskIo {
	private static final int SEGMENT_SIZE = 2;
	protected static final String EVENT_FILE_SUFFIX = ".json.gzip";
	protected static final String REFERENCE_FILE_SUFFIX = ".references.json.gzip";
	protected final ObjectMapper objectMapper;
	protected Path eventRootPath;

	public EventDiskIo(String storageRoot) {
		eventRootPath = get(storageRoot.toString(), "events");
		objectMapper = buildEventMapper();
	}

	private ObjectMapper buildEventMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		final SimpleModule module = new SimpleModule();
		module.addSerializer(Event.class, new EventSerializer());
		module.addDeserializer(Event.class, new EventDeserializer());
		objectMapper.registerModule(module);
		return objectMapper;
	}

	public boolean canRead() {
		return exists(eventRootPath);
	}

	protected void ensurePresenceOfEventRoot() {
		eventRootPath.toFile().mkdirs();
	}

	protected Path createEventPath(final String uid) {
		Path pathToFolder = getPathToEntity(uid);
		pathToFolder.toFile().mkdirs();
		return get(pathToFolder.toString(), uid + EVENT_FILE_SUFFIX);
	}

	protected Path createEventReferencesPath(final String uid) {
		Path pathToFolder = getPathToEntity(uid);
		pathToFolder.toFile().mkdirs();
		return get(pathToFolder.toString(), uid + REFERENCE_FILE_SUFFIX);
	}

	protected Path getEventPath(final String uid) {
		return get(getPathToEntity(uid).toString(), uid + EVENT_FILE_SUFFIX);
	}

	protected Path getEventReferencesPath(final String uid) {
		return get(getPathToEntity(uid).toString(), uid + REFERENCE_FILE_SUFFIX);
	}

	private Path getPathToEntity(final String uid) {
		final Path path = get(eventRootPath.toString(), getPathSegment(uid, 0), getPathSegment(uid, 1));
		return path;
	}

	private String getPathSegment(final String uid, final int segment) {
		return uid.substring(SEGMENT_SIZE * segment, SEGMENT_SIZE * (segment + 1));
	}
}