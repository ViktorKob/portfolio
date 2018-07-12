package net.thomas.portfolio.shared_objects.hbase_index.transformation;

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

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaImpl;

public class WorldControl {

	public HbaseIndexSchema importSchema() {
		final HbaseIndexSchemaImpl schema = readFromFile("schema.json", HbaseIndexSchemaImpl.class);
		return schema;
	}

	public boolean canImportWorld() {
		return exists(getPath("world.json"));
	}

	public World importWorld() {
		return readFromFile("world.json", World.class);
	}

	public void exportWorld(HbaseIndexSchema schema, World world) {
		writeToFile("schema.json", schema);
		writeToFile("world.json", world);
	}

	@SuppressWarnings("unchecked")
	private <T> T readFromFile(final String fileName, Class<?> dataType) {
		try (InputStream dataStream = new GZIPInputStream(new FileInputStream(getPath(fileName).toFile()))) {
			return (T) new ObjectMapper().readValue(dataStream, dataType);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to import data from file " + fileName, e);
		}
	}

	private void writeToFile(final String fileName, final Object outputData) {
		get(".", "src", "main", "resources", "data").toFile()
			.mkdirs();
		try (final OutputStream outputStream = new GZIPOutputStream(new FileOutputStream(getPath(fileName).toFile()))) {
			new ObjectMapper().writeValue(outputStream, outputData);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to export data to file " + fileName, e);
		}
	}

	private Path getPath(String fileName) {
		return get(".", "src", "main", "resources", "data", fileName + ".gzip");
	}
}