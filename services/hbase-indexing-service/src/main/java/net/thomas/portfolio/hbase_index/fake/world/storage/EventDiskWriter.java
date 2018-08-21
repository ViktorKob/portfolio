package net.thomas.portfolio.hbase_index.fake.world.storage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;

public class EventDiskWriter extends EventDiskIo implements EventWriter {

	public EventDiskWriter(String storageRoot) {
		super(storageRoot);
	}

	public void writeToDisk(final Collection<Event> events, final Map<String, References> references) {
		ensurePresenceOfEventRoot();
		for (final Event event : events) {
			add(event);
			add(event.uid, references.get(event.uid));
		}
	}

	public void add(final Event event) {
		final Path filePath = createEventPath(event.uid);
		try (final OutputStream outputStream = new GZIPOutputStream(new FileOutputStream(filePath.toFile()))) {
			objectMapper.writeValue(outputStream, event);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to export data to file " + filePath, e);
		}
	}

	public void add(final String uid, final References references) {
		final Path filePath = createEventReferencesPath(uid);
		try (final OutputStream outputStream = new GZIPOutputStream(new FileOutputStream(filePath.toFile()))) {
			objectMapper.writeValue(outputStream, references);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to export data to file " + filePath, e);
		}
	}
}