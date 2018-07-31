package net.thomas.portfolio.hbase_index.fake.world;

import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.processing.EventDeserializer;
import net.thomas.portfolio.hbase_index.schema.processing.EventSerializer;

public class WorldIoControl {

	private final ObjectMapper visitorBasedMapper;

	public WorldIoControl() {
		visitorBasedMapper = new ObjectMapper();
		final SimpleModule module = new SimpleModule();
		module.addSerializer(Event.class, new EventSerializer());
		module.addDeserializer(Event.class, new EventDeserializer());
		visitorBasedMapper.registerModule(module);
	}

	public boolean canImportWorld() {
		return exists(getPath("world.json"));
	}

	public World importWorld() {
		return readFromFile("world.json", World.class);
	}

	public void exportWorld(World world) {
		writeToFile("world.json", world);
	}

	@SuppressWarnings("unchecked")
	private <T> T readFromFile(final String fileName, Class<?> dataType) {
		try (InputStream dataStream = new GZIPInputStream(new FileInputStream(getPath(fileName).toFile()))) {
			return (T) visitorBasedMapper.readValue(dataStream, dataType);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to import data from file " + fileName, e);
		}
	}

	private void writeToFile(final String fileName, final Object outputData) {
		get(".", "src", "main", "resources", "data").toFile()
			.mkdirs();
		try (final OutputStream outputStream = new GZIPOutputStream(new FileOutputStream(getPath(fileName).toFile()))) {
			visitorBasedMapper.writeValue(outputStream, outputData);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to export data to file " + fileName, e);
		}
	}

	private Path getPath(String fileName) {
		return get(".", "src", "main", "resources", "data", fileName + ".gzip");
	}
}