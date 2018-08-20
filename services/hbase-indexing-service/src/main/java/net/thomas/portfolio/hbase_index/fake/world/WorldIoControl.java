package net.thomas.portfolio.hbase_index.fake.world;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.list;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toCollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.processing.EventDeserializer;
import net.thomas.portfolio.hbase_index.schema.processing.EventSerializer;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;

public class WorldIoControl {
	private static final int SEGMENT_SIZE = 2;
	private static final String EVENT_FILE_SUFFIX = ".json.gzip";
	private static final String REFERENCE_FILE_SUFFIX = ".references.json.gzip";
	private final ObjectMapper visitorBasedMapper;
	private Path eventRootPath;

	public WorldIoControl(String storageRoot) {
		eventRootPath = get(storageRoot.toString(), "events");
		visitorBasedMapper = new ObjectMapper();
		final SimpleModule module = new SimpleModule();
		module.addSerializer(Event.class, new EventSerializer());
		module.addDeserializer(Event.class, new EventDeserializer());
		visitorBasedMapper.registerModule(module);
	}

	public boolean canImportWorld() {
		return exists(eventRootPath);
	}

	public WorldAccess getWorldAccess() {
		return new WorldAccessImpl();
	}

	public void exportWorld(final World world) {
		writeToDisk(world.getEvents(), world.getSourceReferences());
	}

	private void writeToDisk(final Collection<Event> events, final Map<String, References> references) {
		eventRootPath.toFile().mkdirs();
		for (final Event event : events) {
			writeToDisk(event);
			writeToDisk(event.uid, references.get(event.uid));
		}
	}

	private void writeToDisk(final Event event) {
		final Path filePath = createEventPath(event.uid);
		try (final OutputStream outputStream = new GZIPOutputStream(new FileOutputStream(filePath.toFile()))) {
			visitorBasedMapper.writeValue(outputStream, event);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to export data to file " + filePath, e);
		}
	}

	private void writeToDisk(final String uid, final References references) {
		final Path filePath = createEventReferencesPath(uid);
		try (final OutputStream outputStream = new GZIPOutputStream(new FileOutputStream(filePath.toFile()))) {
			visitorBasedMapper.writeValue(outputStream, references);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to export data to file " + filePath, e);
		}
	}

	private Path createEventPath(final String uid) {
		Path pathToFolder = getPathToEntity(uid);
		pathToFolder.toFile().mkdirs();
		return get(pathToFolder.toString(), uid + EVENT_FILE_SUFFIX);
	}

	private Path createEventReferencesPath(final String uid) {
		Path pathToFolder = getPathToEntity(uid);
		pathToFolder.toFile().mkdirs();
		return get(pathToFolder.toString(), uid + REFERENCE_FILE_SUFFIX);
	}

	private Path getEventPath(final String uid) {
		return get(getPathToEntity(uid).toString(), uid + EVENT_FILE_SUFFIX);
	}

	private Path getEventReferencesPath(final String uid) {
		return get(getPathToEntity(uid).toString(), uid + REFERENCE_FILE_SUFFIX);
	}

	private Path getPathToEntity(final String uid) {
		final Path path = get(eventRootPath.toString(), getPathSegment(uid, 0), getPathSegment(uid, 1));
		return path;
	}

	private String getPathSegment(final String uid, final int segment) {
		return uid.substring(SEGMENT_SIZE * segment, SEGMENT_SIZE * (segment + 1));
	}

	public class WorldAccessImpl implements WorldAccess {

		@Override
		public Event getEvent(final String uid) {
			final Path pathToDataType = getEventPath(uid);
			if (exists(pathToDataType)) {
				return read(pathToDataType.toFile(), Event.class);
			} else {
				return null;
			}
		}

		@Override
		public References getReferences(final String uid) {
			final Path pathToDataType = getEventReferencesPath(uid);
			if (exists(pathToDataType)) {
				return read(pathToDataType.toFile(), References.class);
			} else {
				return new References();
			}
		}

		@Override
		public Iterator<Event> iterator() {
			return new WorldIterator();
		}
	}

	public class WorldIterator implements Iterator<Event> {
		private final Queue<Path> rootFolderQueue;
		private Queue<Path> subFolderQueue;
		private Queue<Path> fileQueue;
		private File next;

		public WorldIterator() {
			rootFolderQueue = readFolderList(eventRootPath);
			subFolderQueue = readFolderList(rootFolderQueue.poll());
			fileQueue = readFileList(subFolderQueue.poll());
			next = determineNextFile();
		}

		private Queue<Path> readFolderList(final Path path) {
			if (path != null) {
				try {
					return list(path).map(file -> file.toAbsolutePath()).collect(toCollection(LinkedList::new));
				} catch (final IOException e) {
					throw new RuntimeException("Unable to access data on disk", e);
				}
			} else {
				return null;
			}
		}

		private Queue<Path> readFileList(final Path path) {
			if (path != null) {
				try {
					return list(path).filter(file -> matchesEventFile(file)).map(file -> file.toAbsolutePath())
							.collect(toCollection(LinkedList::new));
				} catch (final IOException e) {
					throw new RuntimeException("Unable to access data on disk", e);
				}
			} else {
				return null;
			}
		}

		private boolean matchesEventFile(final Path file) {
			final String fileAsString = file.toString();
			return fileAsString.endsWith(EVENT_FILE_SUFFIX) && !fileAsString.endsWith(REFERENCE_FILE_SUFFIX);
		}

		private File determineNextFile() {
			while (fileQueue.isEmpty() && !rootFolderQueue.isEmpty()) {
				while (subFolderQueue.isEmpty() && !rootFolderQueue.isEmpty()) {
					subFolderQueue = readFolderList(rootFolderQueue.poll());
				}
				while (fileQueue.isEmpty() && !subFolderQueue.isEmpty()) {
					fileQueue = readFileList(subFolderQueue.poll());
				}
			}
			if (fileQueue.isEmpty()) {
				return null;
			} else {
				return fileQueue.poll().toFile();
			}
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Event next() {
			if (next == null) {
				throw new NoSuchElementException("Iterated beyond final event in data set");
			}
			final Event nextEvent = read(next, Event.class);
			next = determineNextFile();
			return nextEvent;
		}

	}

	private <T> T read(final File file, final Class<T> contentClass) {
		try (final InputStream inputStream = new GZIPInputStream(new FileInputStream(file))) {
			return visitorBasedMapper.readValue(inputStream, contentClass);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to access data on disk", e);
		}
	}
}