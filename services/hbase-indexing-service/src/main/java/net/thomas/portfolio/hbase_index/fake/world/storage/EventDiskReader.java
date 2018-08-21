package net.thomas.portfolio.hbase_index.fake.world.storage;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.list;
import static java.util.stream.Collectors.toCollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.zip.GZIPInputStream;

import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;

public class EventDiskReader extends EventDiskIo implements EventReader {
	public EventDiskReader(String storageRoot) {
		super(storageRoot);
	}

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
			return objectMapper.readValue(inputStream, contentClass);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to access data on disk", e);
		}
	}
}